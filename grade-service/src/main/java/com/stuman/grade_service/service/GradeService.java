package com.stuman.grade_service.service;

import com.stuman.grade_service.dto.request.CreateGradeRequest;
import com.stuman.grade_service.dto.request.UpdateGradeRequest;
import com.stuman.grade_service.dto.response.GpaResponse;
import com.stuman.grade_service.dto.response.GradeResponse;
import com.stuman.grade_service.entity.Semester;
import org.springframework.data.domain.Page;

public interface GradeService {

    // TEACHER
    GradeResponse createGrade(CreateGradeRequest request, Long teacherId);

    GradeResponse updateGrade(Long gradeId,
                              UpdateGradeRequest request,
                              Long teacherId);

    Page<GradeResponse> getGradesForTeacher(
            Long studentId,
            Semester semester,
            Integer year,
            Long subjectId,
            int page,
            int size,
            Long teacherId
    );

    // STUDENT
    Page<GradeResponse> getGradesByStudent(
            Long studentId,
            Semester semester,
            Integer year,
            Long subjectId,
            int page,
            int size
    );

    // ADMIN
    Page<GradeResponse> getAllGrades(
            Long studentId,
            Semester semester,
            Integer year,
            Long subjectId,
            int page,
            int size
    );

    // GPA
    GpaResponse calculateGpa(Long studentId,
                             Semester semester,
                             Integer year);

    GpaResponse calculateGpaForStaff(
            Long studentId,
            Semester semester,
            Integer year,
            Long staffId
    );
}