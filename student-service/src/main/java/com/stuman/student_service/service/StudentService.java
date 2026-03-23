package com.stuman.student_service.service;

import com.stuman.student_service.dto.request.StudentCreateRequest;
import com.stuman.student_service.dto.request.StudentSelfUpdateRequest;
import com.stuman.student_service.dto.request.StudentUpdateRequest;
import com.stuman.student_service.dto.response.StudentPageResponse;
import com.stuman.student_service.dto.response.StudentResponse;
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
