package com.stuman.student_service.controller;

import com.stuman.student_service.dto.request.StudentCreateRequest;
import com.stuman.student_service.dto.request.StudentSelfUpdateRequest;
import com.stuman.student_service.dto.request.StudentUpdateRequest;
import com.stuman.student_service.dto.response.StudentPageResponse;
import com.stuman.student_service.dto.response.StudentResponse;
import com.stuman.student_service.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // Create new Student
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(
            @Valid @RequestBody StudentCreateRequest request
    ) {
        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update Student by ID
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable("id") Long id,
            @Valid @RequestBody StudentUpdateRequest request
    ) {
        StudentResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(response);
    }

    // Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(
            @PathVariable("id") Long id
    ) {
        StudentResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(response);
    }

    // Delete Student by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable("id") Long id
    ) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
    // Get my Profile
    @GetMapping("/me")
    public StudentResponse getMyProfile() {
        return studentService.getMyProfile();
    }
    // Update my Profile
    @PutMapping("/me")
    public ResponseEntity<StudentResponse> updateMyProfile(
            @Valid @RequestBody StudentSelfUpdateRequest request
    ) {
        return ResponseEntity.ok(studentService.updateMyProfile(request));
    }



    // Get students with paging
    @GetMapping
    public StudentPageResponse getStudents(Pageable pageable) {
        return studentService.getStudents(pageable);
    }

}

