package com.educore.grade.service.impl;

import com.educore.common.GradeCreatedEvent;
import com.educore.grade.dto.request.CreateGradeRequest;
import com.educore.grade.dto.request.UpdateGradeRequest;
import com.educore.grade.dto.response.GradeResponse;
import com.educore.grade.entity.Grade;
import com.educore.grade.entity.Semester;
import com.educore.grade.entity.Subject;
import com.educore.grade.exception.BaseException;
import com.educore.grade.exception.ErrorCode;
import com.educore.grade.integration.feign.StudentClient;
import com.educore.grade.integration.kafka.GradeKafkaProducer;
import com.educore.grade.repository.GradeRepository;
import com.educore.grade.repository.SubjectRepository;
import com.educore.grade.repository.TeachingAssignmentRepository;
import com.educore.grade.service.GradeService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final TeachingAssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentClient studentClient;
    private final GradeKafkaProducer kafkaProducer;
    // ================= CREATE ===================
    @Override
    @Transactional
    public GradeResponse createGrade(CreateGradeRequest request, Long teacherId) {
        validateTeacherAssignment(teacherId, request.getSubjectId());
        validateStudent(request.getStudentId());
        Grade grade = Grade.builder()
                .studentId(request.getStudentId())
                .subjectId(request.getSubjectId())
                .type(request.getType())
                .score(request.getScore())
                .weight(request.getWeight())
                .semester(request.getSemester())
                .academicYear(request.getAcademicYear())
                .createdBy(teacherId)
                .createdAt(LocalDateTime.now())
                .build();
        Grade saved = gradeRepository.save(grade);
        //SEND KAFKA
        sendKafkaEvent(saved);
        return mapToResponse(saved);
    }
    // ================= UPDATE ===================
    @Override
    @Transactional
    public GradeResponse updateGrade(Long gradeId,
                                     UpdateGradeRequest request,
                                     Long teacherId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() ->
                        new BaseException(ErrorCode.GRADE_NOT_FOUND));
        validateTeacherAssignment(teacherId, grade.getSubjectId());
        if (request.getScore() != null)
            grade.setScore(request.getScore());
        if (request.getWeight() != null)
            grade.setWeight(request.getWeight());
        if (request.getSemester() != null)
            grade.setSemester(request.getSemester());
        if (request.getAcademicYear() != null)
            grade.setAcademicYear(request.getAcademicYear());
        grade.setUpdatedBy(teacherId);
        grade.setUpdatedAt(LocalDateTime.now());
        Grade saved = gradeRepository.save(grade);
        //SEND KAFKA (UPSERT)
        sendKafkaEvent(saved);
        return mapToResponse(saved);
    }

    // ================= GET ===================

    @Override
    public Page<GradeResponse> getGradesForTeacher(
            Long studentId,
            Semester semester,
            Integer academicYear,
            Long subjectId,
            int page,
            int size,
            Long teacherId) {
        Set<Long> assignedSubjects = getAssignedSubjects(teacherId);
        if (assignedSubjects.isEmpty()) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }
        return getFilteredGrades(
                studentId, semester, academicYear,
                subjectId, assignedSubjects, page, size
        );
    }

    @Override
    public Page<GradeResponse> getGradesByStudent(
            Long userId,
            Semester semester,
            Integer academicYear,
            Long subjectId,
            int page,
            int size) {
        Long studentId = studentClient.getStudentByUserId(userId).getId();
        return getFilteredGrades(
                studentId, semester, academicYear,
                subjectId, null, page, size
        );
    }

    @Override
    public Page<GradeResponse> getAllGrades(
            Long studentId,
            Semester semester,
            Integer academicYear,
            Long subjectId,
            int page,
            int size) {
        return getFilteredGrades(
                studentId, semester, academicYear,
                subjectId, null, page, size
        );
    }

    // ================= PRIVATE ===================
    private void sendKafkaEvent(Grade grade) {
        GradeCreatedEvent event =
                GradeCreatedEvent.builder()
                        .gradeId(grade.getId())
                        .studentId(grade.getStudentId())
                        .subjectId(grade.getSubjectId())
                        .type(grade.getType().name())
                        .score(grade.getScore())
                        .weight(grade.getWeight())
                        .credit(getCredit(grade.getSubjectId()))
                        .semester(grade.getSemester().name())
                        .academicYear(grade.getAcademicYear())
                        .build();
        kafkaProducer.sendGradeCreatedEvent(event);
    }
    private void validateTeacherAssignment(Long teacherId, Long subjectId) {
        boolean assigned =
                assignmentRepository.existsByTeacherIdAndSubjectId(
                        teacherId, subjectId);
        if (!assigned) {
            throw new BaseException(ErrorCode.TEACHER_NOT_ASSIGNED);
        }
    }
    private Set<Long> getAssignedSubjects(Long teacherId) {
        return assignmentRepository.findByTeacherId(teacherId).stream()
                .map(a -> a.getSubjectId())
                .collect(Collectors.toSet());
    }
    private Page<GradeResponse> getFilteredGrades(
            Long studentId,
            Semester semester,
            Integer academicYear,
            Long subjectId,
            Set<Long> allowedSubjects,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        Specification<Grade> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (studentId != null)
                predicates.add(cb.equal(root.get("studentId"), studentId));
            if (semester != null)
                predicates.add(cb.equal(root.get("semester"), semester));
            if (academicYear != null)
                predicates.add(cb.equal(root.get("academicYear"), academicYear));
            if (subjectId != null)
                predicates.add(cb.equal(root.get("subjectId"), subjectId));
            if (allowedSubjects != null)
                predicates.add(root.get("subjectId").in(allowedSubjects));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return gradeRepository.findAll(spec, pageable)
                .map(this::mapToResponse);
    }
    private Integer getCredit(Long subjectId) {
        return subjectRepository.findById(subjectId)
                .map(Subject::getCredit)
                .orElse(3);
    }
    private GradeResponse mapToResponse(Grade grade) {
        String subjectName = subjectRepository.findById(grade.getSubjectId())
                .map(Subject::getName)
                .orElse(null);
        return GradeResponse.builder()
                .id(grade.getId())
                .studentId(grade.getStudentId())
                .subjectId(grade.getSubjectId())
                .subjectName(subjectName)
                .type(grade.getType())
                .score(grade.getScore())
                .weight(grade.getWeight())
                .semester(grade.getSemester())
                .academicYear(grade.getAcademicYear())
                .build();
    }
    private void validateStudent(Long studentId) {
        try {
            studentClient.getStudentById(studentId);
        } catch (Exception e) {
            throw new BaseException(ErrorCode.STUDENT_NOT_FOUND);
        }
    }
}