package com.stuman.grade_service.controller;

import com.stuman.grade_service.dto.request.AssignTeacherRequest;
import com.stuman.grade_service.dto.response.TeachingAssignmentResponse;
import com.stuman.grade_service.dto.response.UserInfoResponse;
import com.stuman.grade_service.entity.TeachingAssignment;
import com.stuman.grade_service.exception.BaseException;
import com.stuman.grade_service.exception.ErrorCode;
import com.stuman.grade_service.integration.feign.AuthClient;
import com.stuman.grade_service.repository.TeachingAssignmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
@Tag(name = "Teaching Assignment API", description = "Operations for managing teaching assignments")
public class TeachingAssignmentController {

    private final TeachingAssignmentRepository repository;
    private final AuthClient authClient;

    @Operation(summary = "Assign teacher to subject (ADMIN only)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> assign(
            @Valid @RequestBody AssignTeacherRequest request
    ) {

        // ===== VERIFY USER FROM AUTH SERVICE =====
        UserInfoResponse user = authClient.getUserById(request.getTeacherId());
        if (user == null || !user.getRoles().contains("TEACHER")) {
            throw new BaseException(ErrorCode.INVALID_TEACHER);
        }

        // ===== CHECK DUPLICATE =====
        if (repository.existsByTeacherIdAndSubjectId(request.getTeacherId(), request.getSubjectId())) {
            throw new BaseException(ErrorCode.ASSIGNMENT_ALREADY_EXISTS);
        }

        TeachingAssignment assignment = TeachingAssignment.builder()
                .teacherId(request.getTeacherId())
                .subjectId(request.getSubjectId())
                .build();

        TeachingAssignment saved = repository.save(assignment);

        return ResponseEntity.status(201).body(Map.of(
                "id", saved.getId(),
                "teacherId", saved.getTeacherId(),
                "subjectId", saved.getSubjectId()
        ));
    }

    @Operation(summary = "Get assignments by teacher ID (ADMIN or the teacher)")
    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasRole('ADMIN') or #teacherId == authentication.principal.id")
    public ResponseEntity<List<TeachingAssignmentResponse>> getByTeacher(
            @PathVariable Long teacherId
    ) {
        List<TeachingAssignment> assignments = repository.findByTeacherId(teacherId);

        List<TeachingAssignmentResponse> response = assignments.stream()
                .map(a -> TeachingAssignmentResponse.builder()
                        .teacherId(a.getTeacherId())
                        .subjectId(a.getSubjectId())
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }
}