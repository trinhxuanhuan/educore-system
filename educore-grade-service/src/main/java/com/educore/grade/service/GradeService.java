package com.educore.grade.service;

import com.educore.grade.dto.request.CreateGradeRequest;
import com.educore.grade.dto.request.UpdateGradeRequest;
import com.educore.grade.dto.response.GradeResponse;
import com.educore.grade.entity.Semester;
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

}