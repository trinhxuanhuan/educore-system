package com.stuman.grade_service.controller;

import com.stuman.grade_service.dto.request.CreateGradeRequest;
import com.stuman.grade_service.dto.request.UpdateGradeRequest;
import com.stuman.grade_service.dto.response.GradeResponse;
import com.stuman.grade_service.entity.Semester;
import com.stuman.grade_service.security.CustomUserPrincipal;
import com.stuman.grade_service.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
@Tag(name = "Grade API", description = "Operations for managing grades")
public class GradeController {

    private final GradeService gradeService;

    // ================= TEACHER ACTIONS ===================
    @Operation(summary = "Create a new grade (TEACHER only)")
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<GradeResponse> createGrade(
            @Valid @RequestBody CreateGradeRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        GradeResponse response = gradeService.createGrade(request, principal.getUserId());
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Update a grade by ID (TEACHER only)")
    @PutMapping("/{gradeId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<GradeResponse> updateGrade(
            @PathVariable("gradeId") Long gradeId,
            @Valid @RequestBody UpdateGradeRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        GradeResponse response = gradeService.updateGrade(gradeId, request, principal.getUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get grades of a student (TEACHER only)")
    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Page<GradeResponse>> getGradesForStudent(
            @PathVariable("studentId") Long studentId,
            @RequestParam(value = "semester", required = false) Semester semester,
            @RequestParam(value = "academicYear", required = false) Integer academicYear,
            @RequestParam(value = "subjectId", required = false) Long subjectId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ){
        Page<GradeResponse> grades = gradeService.getGradesForTeacher(
                studentId, semester, academicYear, subjectId, page, size, principal.getUserId()
        );
        return ResponseEntity.ok(grades);
    }

    // ================= STUDENT ACTIONS ===================
    @Operation(summary = "Get my grades (STUDENT only)")
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Page<GradeResponse>> getMyGrades(
            @RequestParam(value = "semester", required = false) Semester semester,
            @RequestParam(value = "academicYear", required = false) Integer academicYear,
            @RequestParam(value = "subjectId", required = false) Long subjectId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Page<GradeResponse> grades = gradeService.getGradesByStudent(
                principal.getUserId(), semester, academicYear, subjectId, page, size
        );
        return ResponseEntity.ok(grades);
    }

    // ================= ADMIN ACTIONS =====================
    @Operation(summary = "Get all grades (ADMIN only)")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<GradeResponse>> getAllGrades(
            @RequestParam(value = "studentId", required = false) Long studentId,
            @RequestParam(value = "semester", required = false) Semester semester,
            @RequestParam(value = "academicYear", required = false) Integer academicYear,
            @RequestParam(value = "subjectId", required = false) Long subjectId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<GradeResponse> grades = gradeService.getAllGrades(
                studentId, semester, academicYear, subjectId, page, size
        );
        return ResponseEntity.ok(grades);
    }
}