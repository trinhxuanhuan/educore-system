package com.educore.student.service;

import com.educore.student.dto.request.StudentCreateRequest;
import com.educore.student.dto.request.StudentSelfUpdateRequest;
import com.educore.student.dto.request.StudentUpdateRequest;
import com.educore.student.dto.response.StudentPageResponse;
import com.educore.student.dto.response.StudentResponse;
import org.springframework.data.domain.Pageable;

public interface StudentService {

    StudentResponse createStudent(StudentCreateRequest request);

    StudentResponse updateStudent(Long id, StudentUpdateRequest request);

    StudentResponse getStudentById(Long id);

    StudentResponse getMyProfile();

    StudentResponse updateMyProfile(StudentSelfUpdateRequest request);

    StudentPageResponse getStudents(Pageable pageable);

    void deleteStudent(Long id);
    StudentResponse getStudentByUserId(Long userId);
}
