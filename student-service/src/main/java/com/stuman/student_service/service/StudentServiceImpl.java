package com.stuman.student_service.service;

import com.stuman.common_event.StudentCreatedEvent;
import com.stuman.student_service.dto.request.StudentCreateRequest;
import com.stuman.student_service.dto.request.StudentSelfUpdateRequest;
import com.stuman.student_service.dto.request.StudentUpdateRequest;
import com.stuman.student_service.dto.response.AuthUserResponse;
import com.stuman.student_service.dto.response.StudentPageResponse;
import com.stuman.student_service.dto.response.StudentResponse;
import com.stuman.student_service.entity.Student;
import com.stuman.student_service.exception.AppException;
import com.stuman.student_service.exception.ErrorCode;
import com.stuman.student_service.integration.feign.AuthClient;
import com.stuman.student_service.integration.kafka.StudentKafkaProducer;
import com.stuman.student_service.mapper.StudentMapper;
import com.stuman.student_service.repository.StudentRepository;
import com.stuman.student_service.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final AuthClient authClient;
    private final StudentKafkaProducer kafkaProducer;

    // ===================== COMMON =====================
    private CustomUserPrincipal currentUser() {
        return (CustomUserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private void requireRole(String role) {
        if (!currentUser().hasRole(role)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
    }
    private String generateStudentCode() {

        int year = LocalDate.now().getYear(); // 2026
        String prefix = String.valueOf(year);

        return studentRepository
                .findTopByStudentCodeStartingWithOrderByStudentCodeDesc(prefix)
                .map(student -> {
                    String lastCode = student.getStudentCode();
                    long next = Long.parseLong(lastCode) + 1;
                    return String.valueOf(next);
                })
                .orElse(prefix + "000001"); // sinh viên đầu tiên của năm
    }


    // ===================== CREATE =====================
    @Override
    public StudentResponse createStudent(StudentCreateRequest request) {

        requireRole("ADMIN");

        if (studentRepository.existsByUserId(request.getUserId())) {
            throw new AppException(ErrorCode.STUDENT_ALREADY_EXISTS);
        }

        // gọi auth-service
        AuthUserResponse authUser;
        try {
            authUser = authClient.getUserById(request.getUserId());
        } catch (Exception e) {
            throw new AppException(ErrorCode.AUTH_SERVICE_UNAVAILABLE);
        }

        if (authUser == null || authUser.getEmail() == null) {
            throw new AppException(ErrorCode.AUTH_USER_NOT_FOUND);
        }

        if (studentRepository.existsByEmail(authUser.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // map entity
        Student student = studentMapper.toEntity(request);
        student.setUserId(request.getUserId());
        student.setEmail(authUser.getEmail());
        student.setStudentCode(generateStudentCode());

        // save DB
        Student savedStudent = studentRepository.save(student);

        // create event
        StudentCreatedEvent event =
                StudentCreatedEvent.builder()
                        .studentId(savedStudent.getId())
                        .studentCode(savedStudent.getStudentCode())
                        .fullName(savedStudent.getFullName())
                        .email(savedStudent.getEmail())
                        .className(savedStudent.getClassName())
                        .build();

        // publish kafka
        kafkaProducer.sendStudentCreatedEvent(event);

        // return response
        return studentMapper.toResponse(savedStudent);
    }
    @Override
    public StudentResponse getStudentByUserId(Long userId) {

        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        return studentMapper.toResponse(student);
    }




    // ===================== GET BY ID =====================
    @Override
    public StudentResponse getStudentById(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        CustomUserPrincipal user = currentUser();

        boolean canView =
                user.hasRole("ADMIN")
                        || user.hasRole("TEACHER")
                        || student.getUserId().equals(user.getUserId());

        if (!canView) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return studentMapper.toResponse(student);
    }

    // ===================== /students/me =====================
    @Override
    public StudentResponse getMyProfile() {

        requireRole("STUDENT");

        Student student = studentRepository
                .findByUserId(currentUser().getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        return studentMapper.toResponse(student);
    }

    // ===================== /students/me UPDATE =====================
    @Override
    public StudentResponse updateMyProfile(StudentSelfUpdateRequest request) {

        requireRole("STUDENT");

        Student student = studentRepository
                .findByUserId(currentUser().getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        if (request.getEmail() != null &&
                studentRepository.existsByEmailAndIdNot(
                        request.getEmail(), student.getId())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        studentMapper.updateSelf(student, request);

        return studentMapper.toResponse(
                studentRepository.save(student)
        );
    }

    // ===================== ADMIN UPDATE =====================
    @Override
    public StudentResponse updateStudent(Long id, StudentUpdateRequest request) {

        requireRole("ADMIN");

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        if (request.getEmail() != null &&
                studentRepository.existsByEmailAndIdNot(
                        request.getEmail(), student.getId())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (request.getStudentCode() != null &&
                studentRepository.existsByStudentCodeAndIdNot(
                        request.getStudentCode(), student.getId())) {
            throw new AppException(ErrorCode.STUDENT_CODE_ALREADY_EXISTS);
        }

        studentMapper.updateEntity(student, request);

        return studentMapper.toResponse(
                studentRepository.save(student)
        );
    }

    // ===================== PAGE =====================
    @Override
    public StudentPageResponse getStudents(Pageable pageable) {

        CustomUserPrincipal user = currentUser();

        if (!user.hasRole("ADMIN") && !user.hasRole("TEACHER")) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return studentMapper.toPageResponse(
                studentRepository.findAll(pageable)
        );
    }

    // ===================== DELETE =====================
    @Override
    public void deleteStudent(Long id) {

        requireRole("ADMIN");

        if (!studentRepository.existsById(id)) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }

        studentRepository.deleteById(id);
    }
}
