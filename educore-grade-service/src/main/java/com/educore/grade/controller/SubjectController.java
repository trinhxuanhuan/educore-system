package com.educore.grade.controller;

import com.educore.grade.dto.request.CreateSubjectRequest;
import com.educore.grade.dto.response.SubjectResponse;
import com.educore.grade.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
@Tag(name = "Subject API", description = "Operations for managing subjects")
public class SubjectController {

    private final SubjectService subjectService;

    @Operation(summary = "Create a new subject (ADMIN only)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectResponse> createSubject(
            @RequestBody CreateSubjectRequest request
    ) {
        SubjectResponse response = subjectService.createSubject(request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Get all subjects (ADMIN, TEACHER, STUDENT)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<List<SubjectResponse>> getAllSubjects() {
        List<SubjectResponse> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }
}