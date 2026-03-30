package com.stuman.grade_service.service.impl;

import com.stuman.common_event.GradeCreatedEvent;
import com.stuman.grade_service.dto.request.CreateGradeRequest;
import com.stuman.grade_service.dto.request.UpdateGradeRequest;
import com.stuman.grade_service.dto.response.GradeResponse;
import com.stuman.grade_service.dto.response.StudentInternalResponse;
import com.stuman.grade_service.entity.*;
import com.stuman.grade_service.exception.BaseException;
import com.stuman.grade_service.exception.ErrorCode;
import com.stuman.grade_service.integration.feign.StudentClient;
import com.stuman.grade_service.integration.kafka.GradeKafkaProducer;
import com.stuman.grade_service.repository.GradeRepository;
import com.stuman.grade_service.repository.SubjectRepository;
import com.stuman.grade_service.repository.TeachingAssignmentRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class GradeServiceImplTest {

    @Mock private GradeRepository gradeRepository;
    @Mock private TeachingAssignmentRepository assignmentRepository;
    @Mock private SubjectRepository subjectRepository;
    @Mock private StudentClient studentClient;
    @Mock private GradeKafkaProducer kafkaProducer;

    @InjectMocks private GradeServiceImpl gradeService;

    // ================= HELPER =================

    private CreateGradeRequest buildCreateRequest() {
        CreateGradeRequest request = new CreateGradeRequest();
        request.setStudentId(100L);
        request.setSubjectId(10L);
        request.setType(GradeType.MIDTERM);
        request.setScore(9.5);
        request.setWeight(0.4);
        request.setSemester(Semester.FALL);
        request.setAcademicYear(2025);
        return request;
    }

    private Grade buildGrade() {
        return Grade.builder()
                .id(1L)
                .studentId(100L)
                .subjectId(10L)
                .type(GradeType.MIDTERM)
                .score(8.0)
                .weight(0.4)
                .semester(Semester.FALL)
                .academicYear(2025)
                .build();
    }

    private Subject buildSubject() {
        return Subject.builder()
                .id(10L)
                .name("Math")
                .credit(3)
                .build();
    }

    // ================= CREATE =================

    @Test
    void createGrade_success() {
        // Arrange
        Long teacherId = 1L;
        CreateGradeRequest request = buildCreateRequest();

        when(assignmentRepository.existsByTeacherIdAndSubjectId(teacherId, 10L))
                .thenReturn(true);

        when(studentClient.getStudentById(100L)).thenReturn(null);

        when(subjectRepository.findById(10L))
                .thenReturn(Optional.of(buildSubject()));

        when(gradeRepository.save(any()))
                .thenAnswer(inv -> {
                    Grade g = inv.getArgument(0);
                    g.setId(1L);
                    return g;
                });

        // Act
        GradeResponse result = gradeService.createGrade(request, teacherId);

        // Assert
        assertNotNull(result);
        assertEquals(9.5, result.getScore());
        assertEquals("Math", result.getSubjectName());

        verify(kafkaProducer).sendGradeCreatedEvent(any());
    }

    @Test
    void createGrade_teacherNotAssigned_shouldThrow() {
        // Arrange
        when(assignmentRepository.existsByTeacherIdAndSubjectId(any(), any()))
                .thenReturn(false);

        CreateGradeRequest request = buildCreateRequest();

        // Act & Assert
        BaseException ex = assertThrows(BaseException.class,
                () -> gradeService.createGrade(request, 1L));

        assertEquals(ErrorCode.TEACHER_NOT_ASSIGNED, ex.getErrorCode());
    }

    @Test
    void createGrade_studentNotFound_shouldThrow() {
        // Arrange
        when(assignmentRepository.existsByTeacherIdAndSubjectId(any(), any()))
                .thenReturn(true);

        when(studentClient.getStudentById(any()))
                .thenThrow(new RuntimeException());

        CreateGradeRequest request = buildCreateRequest();

        // Act & Assert
        BaseException ex = assertThrows(BaseException.class,
                () -> gradeService.createGrade(request, 1L));

        assertEquals(ErrorCode.STUDENT_NOT_FOUND, ex.getErrorCode());
    }

    // ================= UPDATE =================

    @Test
    void updateGrade_success() {
        // Arrange
        Long teacherId = 1L;
        Grade grade = buildGrade();

        when(gradeRepository.findById(1L))
                .thenReturn(Optional.of(grade));

        when(assignmentRepository.existsByTeacherIdAndSubjectId(teacherId, 10L))
                .thenReturn(true);

        when(subjectRepository.findById(10L))
                .thenReturn(Optional.of(buildSubject()));

        when(gradeRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        UpdateGradeRequest request = new UpdateGradeRequest();
        request.setScore(9.0);

        // Act
        GradeResponse result =
                gradeService.updateGrade(1L, request, teacherId);

        // Assert
        assertEquals(9.0, result.getScore());
        verify(kafkaProducer).sendGradeCreatedEvent(any());
    }

    @Test
    void updateGrade_notFound_shouldThrow() {
        // Arrange
        when(gradeRepository.findById(any()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class,
                () -> gradeService.updateGrade(1L,
                        new UpdateGradeRequest(), 1L));
    }

    @Test
    void updateGrade_teacherNotAssigned_shouldThrow() {
        // Arrange
        Grade grade = buildGrade();

        when(gradeRepository.findById(1L))
                .thenReturn(Optional.of(grade));

        when(assignmentRepository.existsByTeacherIdAndSubjectId(any(), any()))
                .thenReturn(false);

        // Act & Assert
        BaseException ex = assertThrows(BaseException.class,
                () -> gradeService.updateGrade(1L,
                        new UpdateGradeRequest(), 1L));

        assertEquals(ErrorCode.TEACHER_NOT_ASSIGNED, ex.getErrorCode());
    }

    // ================= GET =================

    @Test
    void getGradesForTeacher_noAssignment_shouldThrow() {
        // Arrange
        when(assignmentRepository.findByTeacherId(any()))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(BaseException.class,
                () -> gradeService.getGradesForTeacher(
                        null, null, null, null,
                        0, 10, 1L
                ));
    }

    @Test
    void getGradesByStudent_success() {
        // Arrange
        StudentInternalResponse student = StudentInternalResponse.builder()
                .id(100L)
                .build();

        when(studentClient.getStudentByUserId(1L))
                .thenReturn(student);

        Page<Grade> page = new PageImpl<>(List.of());

        when(gradeRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        // Act
        Page<GradeResponse> result =
                gradeService.getGradesByStudent(
                        1L, null, null, null, 0, 10
                );

        // Assert
        assertNotNull(result);
    }

    // ================= KAFKA =================

    @Test
    void createGrade_shouldSendCorrectKafkaEvent() {
        // Arrange
        Long teacherId = 1L;
        CreateGradeRequest request = buildCreateRequest();
        request.setScore(10.0);

        when(assignmentRepository.existsByTeacherIdAndSubjectId(any(), any()))
                .thenReturn(true);

        when(studentClient.getStudentById(any()))
                .thenReturn(null);

        when(subjectRepository.findById(any()))
                .thenReturn(Optional.of(
                        Subject.builder().id(10L).credit(4).build()
                ));

        when(gradeRepository.save(any()))
                .thenAnswer(inv -> {
                    Grade g = inv.getArgument(0);
                    g.setId(1L);
                    return g;
                });

        ArgumentCaptor<GradeCreatedEvent> captor =
                ArgumentCaptor.forClass(GradeCreatedEvent.class);

        // Act
        gradeService.createGrade(request, teacherId);

        // Assert
        verify(kafkaProducer).sendGradeCreatedEvent(captor.capture());

        GradeCreatedEvent event = captor.getValue();

        assertEquals(10.0, event.getScore());
        assertEquals(4, event.getCredit());
        assertEquals("MIDTERM", event.getType());
    }
}