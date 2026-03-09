package com.stuman.grade_service.controller;

import com.stuman.grade_service.dto.request.CreateGradeRequest;
import com.stuman.grade_service.dto.request.UpdateGradeRequest;
import com.stuman.grade_service.dto.response.GpaResponse;
import com.stuman.grade_service.dto.response.GradeResponse;
import com.stuman.grade_service.entity.Semester;
import com.stuman.grade_service.security.CustomUserPrincipal;
import com.stuman.grade_service.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    // =====================================================
    // ================= TEACHER ACTIONS ===================
    // =====================================================

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public GradeResponse createGrade(
            @Valid @RequestBody CreateGradeRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return gradeService.createGrade(request, principal.getUserId());
    }

    @PutMapping("/{gradeId}")
    @PreAuthorize("hasRole('TEACHER')")
    public GradeResponse updateGrade(
            @PathVariable("gradeId") Long gradeId,
            @Valid @RequestBody UpdateGradeRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return gradeService.updateGrade(gradeId, request, principal.getUserId());
    }

    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public Page<GradeResponse> getGradesForStudent(
            @PathVariable("studentId") Long studentId,
            @RequestParam(required = false) Semester semester,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return gradeService.getGradesForTeacher(
                studentId,
                semester,
                year,
                subjectId,
                page,
                size,
                principal.getUserId()
        );
    }

    // =====================================================
    // ================= STUDENT ACTIONS ===================
    // =====================================================

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public Page<GradeResponse> getMyGrades(
            @RequestParam(required = false) Semester semester,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return gradeService.getGradesByStudent(
                principal.getUserId(),
                semester,
                year,
                subjectId,
                page,
                size
        );
    }

    // =====================================================
    // ================= ADMIN ACTIONS =====================
    // =====================================================

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<GradeResponse> getAllGrades(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Semester semester,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return gradeService.getAllGrades(
                studentId,
                semester,
                year,
                subjectId,
                page,
                size
        );
    }

    // =====================================================
    // ================= GPA ===============================
    // =====================================================

    @GetMapping("/me/gpa")
    @PreAuthorize("hasRole('STUDENT')")
    public GpaResponse getMyGpa(
            @RequestParam Semester semester,
            @RequestParam Integer year,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return gradeService.calculateGpa(
                principal.getUserId(),
                semester,
                year
        );
    }

    @GetMapping("/students/{studentId}/gpa")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public GpaResponse getStudentGpa(
            @PathVariable("studentId") Long studentId,
            @RequestParam Semester semester,
            @RequestParam Integer year,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return gradeService.calculateGpaForStaff(
                studentId,
                semester,
                year,
                principal.getUserId()
        );
    }
}