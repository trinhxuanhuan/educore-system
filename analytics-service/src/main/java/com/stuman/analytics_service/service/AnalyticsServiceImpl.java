package com.stuman.analytics_service.service;

import com.stuman.analytics_service.dto.StudentAnalyticsResponse;
import com.stuman.analytics_service.entity.*;
import com.stuman.analytics_service.exception.BaseException;
import com.stuman.analytics_service.exception.ErrorCode;
import com.stuman.analytics_service.repository.GradeCacheRepository;
import com.stuman.analytics_service.repository.StudentAnalyticsRepository;
import com.stuman.analytics_service.repository.StudentCourseRepository;
import com.stuman.analytics_service.service.AnalyticsService;
import com.stuman.common_event.GradeCreatedEvent;
import com.stuman.common_event.StudentCreatedEvent;
import com.stuman.common_event.StudentDeletedEvent;
import com.stuman.common_event.StudentUpdatedEvent;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final StudentAnalyticsRepository repository;
    private final StudentCourseRepository studentCourseRepository;
    private final GradeCacheRepository gradeCacheRepository;

    private final ApplicationEventPublisher eventPublisher;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // ===================== GET =====================

    @Override
    public List<StudentAnalyticsResponse> getAllStudentAnalytics() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public StudentAnalyticsResponse getStudentAnalytics(Long studentId) {
        StudentAnalytics analytics = repository.findById(studentId)
                .orElseThrow(() ->
                        new BaseException(ErrorCode.STUDENT_ANALYTICS_NOT_FOUND)
                );
        return mapToResponse(analytics);
    }

    @Override
    public List<StudentAnalyticsResponse> getTopStudents(int limit) {
        if (limit <= 0) limit = 10;

        return repository.findAll(
                        PageRequest.of(0, limit,
                                Sort.by(Sort.Order.desc("gpa"),
                                        Sort.Order.asc("studentId")))
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ===================== HANDLE EVENT =====================

    @Override
    @Transactional
    public void handleStudentCreated(StudentCreatedEvent event) {

        if (repository.existsById(event.getStudentId())) return;

        StudentAnalytics analytics = StudentAnalytics.builder()
                .studentId(event.getStudentId())
                .studentCode(event.getStudentCode())
                .fullName(event.getFullName())
                .email(event.getEmail())
                .className(event.getClassName())
                .gpa(0.0)
                .totalSubjects(0)
                .classification(Classification.UNKNOWN)
                .build();

        repository.save(analytics);

        eventPublisher.publishEvent(new StudentAnalyticsCreatedEvent(event.getStudentId()));
    }
    @Override
    @Transactional
    public void handleStudentDeleted(StudentDeletedEvent event) {

        Long studentId = event.getStudentId();
        gradeCacheRepository.deleteByStudentId(studentId);
        studentCourseRepository.deleteByStudentId(studentId);
        repository.deleteById(studentId);
        log.info("Deleted analytics data for studentId={}", studentId);
    }

    @Override
    @Transactional
    public void handleStudentUpdated(StudentUpdatedEvent event) {

        StudentAnalytics analytics = repository.findById(event.getStudentId())
                .orElseThrow(() -> new BaseException(ErrorCode.STUDENT_ANALYTICS_NOT_FOUND));

        analytics.setStudentCode(event.getStudentCode());
        analytics.setFullName(event.getFullName());
        analytics.setEmail(event.getEmail());
        analytics.setClassName(event.getClassName());

        repository.save(analytics);

        log.info("Updated analytics for studentId={}", event.getStudentId());
    }
    @Override
    @Transactional
    public void handleGradeCreated(GradeCreatedEvent event) {
        // ===== VALIDATE DATA =====
        if (event.getScore() == null || event.getWeight() == null) {
            throw new BaseException(ErrorCode.INVALID_GRADE_DATA);
        }
        // ===== VALIDATE STUDENT=====
        if (!repository.existsById(event.getStudentId())) {
            log.warn("Student not found in analytics, skip grade event. studentId={}", event.getStudentId());
            return;
        }
        // ===== UPSERT GRADE CACHE =====
        GradeCache cache = gradeCacheRepository
                .findByGradeId(event.getGradeId())
                .orElseGet(() -> GradeCache.builder()
                        .gradeId(event.getGradeId())
                        .studentId(event.getStudentId())
                        .subjectId(event.getSubjectId())
                        .build()
                );
        cache.setScore(event.getScore());
        cache.setWeight(event.getWeight());
        gradeCacheRepository.save(cache);
        // ===== FINAL SCORE =====
        Double finalScore = calculateFinalScore(
                event.getStudentId(),
                event.getSubjectId()
        );
        // ===== UPSERT COURSE =====
        StudentCourse course = studentCourseRepository
                .findByStudentIdAndSubjectId(
                        event.getStudentId(),
                        event.getSubjectId()
                )
                .orElseGet(() -> StudentCourse.builder()
                        .studentId(event.getStudentId())
                        .subjectId(event.getSubjectId())
                        .build()
                );
        course.setFinalScore(finalScore);
        course.setCredit(event.getCredit());
        studentCourseRepository.save(course);

        // ===== GPA =====
        List<StudentCourse> courses =
                studentCourseRepository.findByStudentId(event.getStudentId());
        double gpa = calculateGpa(courses);
        StudentAnalytics analytics = repository.findById(event.getStudentId())
                .orElseThrow(() ->
                        new BaseException(ErrorCode.STUDENT_ANALYTICS_NOT_FOUND)
                );

        analytics.setGpa(gpa);
        analytics.setTotalSubjects(courses.size());
        analytics.setTotalCredits(
                courses.stream()
                        .mapToInt(c -> c.getCredit() != null ? c.getCredit() : 0)
                        .sum()
        );
        analytics.setAverageScore(
                courses.stream()
                        .filter(c -> c.getFinalScore() != null)
                        .mapToDouble(StudentCourse::getFinalScore)
                        .average()
                        .orElse(0)
        );
        analytics.setClassification(classify(gpa));
        repository.save(analytics);
        // ===== EVENT =====
        eventPublisher.publishEvent(
                new StudentAnalyticsUpdatedEvent(event.getStudentId())
        );
    }
    // ===================== KAFKA =====================
    @TransactionalEventListener
    public void sendKafkaCreated(StudentAnalyticsCreatedEvent event) {
        kafkaTemplate.send("analytics-created-topic", event.studentId());
    }
    @TransactionalEventListener
    public void sendKafkaUpdated(StudentAnalyticsUpdatedEvent event) {
        kafkaTemplate.send("analytics-updated-topic", event.studentId());
    }
    // ===================== UTILS =====================
    private double calculateGpa(List<StudentCourse> courses) {
        if (courses == null || courses.isEmpty()) return 0.0;
        double totalScore = 0;
        int totalCredit = 0;
        for (StudentCourse c : courses) {
            if (c.getFinalScore() != null && c.getCredit() != null) {
                totalScore += c.getFinalScore() * c.getCredit();
                totalCredit += c.getCredit();
            }
        }
        return totalCredit == 0 ? 0.0 : totalScore / totalCredit;
    }
    private Double calculateFinalScore(Long studentId, Long subjectId) {
        List<GradeCache> grades =
                gradeCacheRepository.findByStudentIdAndSubjectId(studentId, subjectId);
        double total = 0;
        double totalWeight = 0;
        for (GradeCache g : grades) {
            total += g.getScore() * g.getWeight();
            totalWeight += g.getWeight();
        }
        return totalWeight == 0 ? 0 : total / totalWeight;
    }
    private Classification classify(Double gpa) {
        if (gpa == null) return Classification.UNKNOWN;
        if (gpa >= 9.0) return Classification.EXCELLENT;
        if (gpa >= 8.5) return Classification.VERY_GOOD;
        if (gpa >= 7.0) return Classification.GOOD;
        if (gpa >= 5.0) return Classification.AVERAGE;
        return Classification.POOR;
    }
    private StudentAnalyticsResponse mapToResponse(StudentAnalytics entity) {
        return StudentAnalyticsResponse.builder()
                .studentId(entity.getStudentId())
                .gpa(entity.getGpa())
                .totalSubjects(entity.getTotalSubjects())
                .classification(
                        entity.getClassification() != null
                                ? entity.getClassification().name().replace("_", " ")
                                : null
                )
                .build();
    }
    // ===================== INTERNAL EVENT =====================
    public record StudentAnalyticsCreatedEvent(Long studentId) {}
    public record StudentAnalyticsUpdatedEvent(Long studentId) {}
}
