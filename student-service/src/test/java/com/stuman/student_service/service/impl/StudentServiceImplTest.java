package com.stuman.student_service.service.impl;
import com.stuman.common_event.StudentCreatedEvent;
import com.stuman.student_service.dto.request.StudentCreateRequest;
import com.stuman.student_service.dto.response.AuthUserResponse;
import com.stuman.student_service.entity.Student;
import com.stuman.student_service.exception.AppException;
import com.stuman.student_service.integration.feign.AuthClient;
import com.stuman.student_service.integration.kafka.StudentKafkaProducer;
import com.stuman.student_service.mapper.StudentMapper;
import com.stuman.student_service.repository.StudentRepository;
import com.stuman.student_service.security.CustomUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private AuthClient authClient;

    @Mock
    private StudentKafkaProducer kafkaProducer;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setupSecurityContext() {
        CustomUserPrincipal principal =
                new CustomUserPrincipal(1L, "admin", Set.of("ADMIN"));

        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // ================= CREATE SUCCESS =================
    @Test
    void createStudent_success() {
        StudentCreateRequest request = StudentCreateRequest.builder()
                .userId(1L)
                .fullName("Test User")
                .className("SE1")
                .build();

        when(studentRepository.existsByUserId(1L)).thenReturn(false);

        AuthUserResponse authUser = new AuthUserResponse();
        authUser.setEmail("test@gmail.com");

        when(authClient.getUserById(1L)).thenReturn(authUser);
        when(studentRepository.existsByEmail("test@gmail.com")).thenReturn(false);

        Student student = new Student();
        student.setId(1L);
        student.setEmail("test@gmail.com");
        student.setStudentCode("2026000001");

        when(studentMapper.toEntity(request)).thenReturn(student);
        when(studentRepository.save(any())).thenReturn(student);

        studentService.createStudent(request);

        verify(studentRepository).save(any());
        verify(kafkaProducer).sendStudentCreatedEvent(any(StudentCreatedEvent.class));
    }

    // ================= CREATE FAIL: USER EXISTS =================
    @Test
    void createStudent_userAlreadyExists() {
        StudentCreateRequest request = StudentCreateRequest.builder()
                .userId(1L)
                .build();

        when(studentRepository.existsByUserId(1L)).thenReturn(true);

        assertThrows(AppException.class,
                () -> studentService.createStudent(request));
    }

    // ================= CREATE FAIL: AUTH SERVICE ERROR =================
    @Test
    void createStudent_authServiceError() {
        StudentCreateRequest request = StudentCreateRequest.builder()
                .userId(1L)
                .build();

        when(studentRepository.existsByUserId(1L)).thenReturn(false);
        when(authClient.getUserById(1L)).thenThrow(RuntimeException.class);

        assertThrows(AppException.class,
                () -> studentService.createStudent(request));
    }

    // ================= GET STUDENT BY ID (ACCESS DENIED) =================
    @Test
    void getStudentById_accessDenied() {
        Student student = new Student();
        student.setId(1L);
        student.setUserId(999L);

        when(studentRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(student));

        // user hiện tại không phải admin/teacher và không phải chủ
        CustomUserPrincipal principal =
                new CustomUserPrincipal(2L, "user", Set.of("STUDENT"));

        when(authentication.getPrincipal()).thenReturn(principal);

        assertThrows(AppException.class,
                () -> studentService.getStudentById(1L));
    }

    // ================= DELETE STUDENT =================
    @Test
    void deleteStudent_success() {
        Student student = new Student();
        student.setId(1L);
        student.setDeleted(false);

        when(studentRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(student));

        studentService.deleteStudent(1L);

        assertTrue(student.isDeleted());
        verify(studentRepository).save(student);
        verify(kafkaProducer).sendStudentDeletedEvent(any());
    }
}

