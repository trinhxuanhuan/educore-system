package com.stuman.grade_service.controller;

import com.stuman.grade_service.dto.request.CreateSubjectRequest;
import com.stuman.grade_service.dto.response.SubjectResponse;
import com.stuman.grade_service.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectResponse createSubject(
            @RequestBody CreateSubjectRequest request
    ) {
        return subjectService.createSubject(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public List<SubjectResponse> getAllSubjects() {
        return subjectService.getAllSubjects();
    }
}
