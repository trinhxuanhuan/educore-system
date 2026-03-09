package com.stuman.student_service.controller;

import com.stuman.student_service.dto.response.StudentInternalResponse;
import com.stuman.student_service.entity.Student;
import com.stuman.student_service.entity.StudentStatus;
import com.stuman.student_service.exception.AppException;
import com.stuman.student_service.exception.ErrorCode;
import com.stuman.student_service.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/students")
@RequiredArgsConstructor
public class StudentInternalController {

    private final StudentRepository studentRepository;
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','SERVICE')")
    public StudentInternalResponse getStudentByIdInternal(
            @PathVariable("id") Long id
    ) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new AppException(ErrorCode.STUDENT_NOT_FOUND)
                );

        return StudentInternalResponse.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .active(student.getStatus() == StudentStatus.ACTIVE)
                .build();
    }
}
