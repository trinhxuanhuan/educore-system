package com.educore.analytics.controller;
import com.educore.analytics.dto.StudentAnalyticsResponse;
import com.educore.analytics.security.CustomUserPrincipal;
import com.educore.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Tag(name = "Analytics API", description = "Operations for student analytics and reporting")
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    // ================= ADMIN =================
    @Operation(summary = "Get analytics for all students (ADMIN only)")
    @GetMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public List<StudentAnalyticsResponse> getAllStudentAnalytics() {
        return analyticsService.getAllStudentAnalytics();
    }
    @Operation(summary = "Get top N students by performance (ADMIN only)")
    @GetMapping("/top")
    @PreAuthorize("hasRole('ADMIN')")
    public List<StudentAnalyticsResponse> getTopStudents(
            @RequestParam(defaultValue = "10") @Positive int limit
    ) {
        return analyticsService.getTopStudents(limit);
    }
    // ================= STAFF =================
    @Operation(summary = "Get analytics for a specific student (ADMIN/TEACHER only)")
    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public StudentAnalyticsResponse getStudentAnalytics(
            @PathVariable Long studentId
    ) {
        return analyticsService.getStudentAnalytics(studentId);
    }
    // ================= STUDENT =================
    @Operation(summary = "Get analytics for the logged-in student")
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentAnalyticsResponse getMyAnalytics(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return analyticsService.getStudentAnalytics(principal.getUserId());
    }

}