package com.stuman.grade_service.service.impl;

import com.stuman.grade_service.dto.request.CreateGradeRequest;
import com.stuman.grade_service.dto.request.UpdateGradeRequest;
import com.stuman.grade_service.dto.response.GpaResponse;
import com.stuman.grade_service.dto.response.GradeResponse;
import com.stuman.grade_service.entity.Grade;
import com.stuman.grade_service.entity.Semester;
import com.stuman.grade_service.exception.BaseException;
import com.stuman.grade_service.exception.ErrorCode;
import com.stuman.grade_service.repository.GradeRepository;
import com.stuman.grade_service.repository.TeachingAssignmentRepository;
import com.stuman.grade_service.service.GradeService;
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

    // ======================================================
    // ================= TEACHER ============================
    // ======================================================

    @Override
    @Transactional
    public GradeResponse createGrade(CreateGradeRequest request, Long teacherId) {

        validateTeacherAssignment(teacherId, request.getSubjectId());

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

        gradeRepository.save(grade);

        return mapToResponse(grade);
    }

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

        return mapToResponse(gradeRepository.save(grade));
    }

    @Override
    public Page<GradeResponse> getGradesForTeacher(
            Long studentId,
            Semester semester,
            Integer academicYear,
            Long subjectId,
            int page,
            int size,
            Long teacherId) {

        // Teacher chỉ được xem subject mình dạy
        Set<Long> assignedSubjects = getAssignedSubjects(teacherId);

        if (assignedSubjects.isEmpty()) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        return getFilteredGrades(
                studentId,
                semester,
                academicYear,
                subjectId,
                assignedSubjects,
                page,
                size
        );
    }

    // ======================================================
    // ================= STUDENT ============================
    // ======================================================

    @Override
    public Page<GradeResponse> getGradesByStudent(
            Long studentId,
            Semester semester,
            Integer academicYear,
            Long subjectId,
            int page,
            int size) {

        return getFilteredGrades(
                studentId,
                semester,
                academicYear,
                subjectId,
                null,
                page,
                size
        );
    }

    // ======================================================
    // ================= ADMIN ==============================
    // ======================================================

    @Override
    public Page<GradeResponse> getAllGrades(
            Long studentId,
            Semester semester,
            Integer academicYear,
            Long subjectId,
            int page,
            int size) {

        return getFilteredGrades(
                studentId,
                semester,
                academicYear,
                subjectId,
                null,
                page,
                size
        );
    }

    // ======================================================
    // ================= GPA ================================
    // ======================================================

    @Override
    public GpaResponse calculateGpa(Long studentId,
                                    Semester semester,
                                    Integer academicYear) {

        List<Grade> grades =
                gradeRepository.findByStudentIdAndSemesterAndAcademicYear(
                        studentId, semester, academicYear);

        double gpa = calculateWeightedGpa(grades);

        return buildGpaResponse(studentId, semester, academicYear, gpa);
    }

    @Override
    public GpaResponse calculateGpaForStaff(
            Long studentId,
            Semester semester,
            Integer academicYear,
            Long staffId) {

        List<Grade> grades =
                gradeRepository.findByStudentIdAndSemesterAndAcademicYear(
                        studentId, semester, academicYear);

        Set<Long> assignedSubjects = getAssignedSubjects(staffId);

        boolean allowed = grades.stream()
                .anyMatch(g -> assignedSubjects.contains(g.getSubjectId()));

        if (!allowed) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        double gpa = calculateWeightedGpa(grades);

        return buildGpaResponse(studentId, semester, academicYear, gpa);
    }

    // ======================================================
    // ================= PRIVATE ============================
    // ======================================================

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

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

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

    private GradeResponse mapToResponse(Grade grade) {
        return GradeResponse.builder()
                .id(grade.getId())
                .studentId(grade.getStudentId())
                .subjectId(grade.getSubjectId())
                .type(grade.getType())
                .score(grade.getScore())
                .weight(grade.getWeight())
                .semester(grade.getSemester())
                .academicYear(grade.getAcademicYear())
                .build();
    }
    private double calculateWeightedGpa(List<Grade> grades) {
        double totalWeight = 0;
        double weightedSum = 0;

        for (Grade g : grades) {
            weightedSum += g.getScore() * g.getWeight();
            totalWeight += g.getWeight();
        }

        return totalWeight == 0 ? 0 : weightedSum / totalWeight;
    }

    private GpaResponse buildGpaResponse(Long studentId,
                                         Semester semester,
                                         Integer academicYear,
                                         double gpa) {
        return GpaResponse.builder()
                .studentId(studentId)
                .semester(semester.name())
                .academicYear(academicYear)
                .gpa(gpa)
                .build();
    }
}