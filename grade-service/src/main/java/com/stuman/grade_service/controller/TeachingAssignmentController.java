package com.stuman.grade_service.controller;

import com.stuman.grade_service.dto.request.AssignTeacherRequest;
import com.stuman.grade_service.dto.response.TeachingAssignmentResponse;
import com.stuman.grade_service.dto.response.UserInfoResponse;
import com.stuman.grade_service.entity.TeachingAssignment;
import com.stuman.grade_service.exception.BaseException;
import com.stuman.grade_service.exception.ErrorCode;
import com.stuman.grade_service.integration.AuthClient;
import com.stuman.grade_service.repository.TeachingAssignmentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class TeachingAssignmentController {

    private final TeachingAssignmentRepository repository;
    private final AuthClient authClient;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assign(
            @Valid @RequestBody AssignTeacherRequest request
    ) {

        // ===== VERIFY USER FROM AUTH SERVICE =====
        UserInfoResponse user = authClient.getUserById(request.getTeacherId());

        if (user == null || !user.getRoles().contains("TEACHER")) {
            throw new BaseException(ErrorCode.INVALID_TEACHER);
        }

        // ===== CHECK DUPLICATE =====
        if (repository.existsByTeacherIdAndSubjectId(
                request.getTeacherId(),
                request.getSubjectId())) {

            throw new BaseException(ErrorCode.ASSIGNMENT_ALREADY_EXISTS);
        }

        TeachingAssignment assignment = TeachingAssignment.builder()
                .teacherId(request.getTeacherId())
                .subjectId(request.getSubjectId())
                .build();

        TeachingAssignment saved = repository.save(assignment);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", saved.getId(),
                "teacherId", saved.getTeacherId(),
                "subjectId", saved.getSubjectId()
        ));
    }
    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasRole('ADMIN') or #teacherId == authentication.principal.id")
    public ResponseEntity<List<TeachingAssignmentResponse>> getByTeacher(
            @PathVariable Long teacherId) {

        List<TeachingAssignment> assignments =
                repository.findByTeacherId(teacherId);

        List<TeachingAssignmentResponse> response = assignments.stream()
                .map(a -> TeachingAssignmentResponse.builder()
                        .teacherId(a.getTeacherId())
                        .subjectId(a.getSubjectId())
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }
}