package com.stuman.analytics_service.service.impl;
import com.stuman.analytics_service.entity.*;
import com.stuman.analytics_service.exception.BaseException;
import com.stuman.analytics_service.repository.GradeCacheRepository;
import com.stuman.analytics_service.repository.StudentAnalyticsRepository;
import com.stuman.analytics_service.repository.StudentCourseRepository;
import com.stuman.common_event.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private StudentAnalyticsRepository repository;

    @Mock
    private StudentCourseRepository studentCourseRepository;

    @Mock
    private GradeCacheRepository gradeCacheRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private AnalyticsServiceImpl service;

    // ===================== CREATE =====================
    @Test
    void handleStudentCreated_success() {
        StudentCreatedEvent event = StudentCreatedEvent.builder()
                .studentId(1L)
                .studentCode("S001")
                .fullName("Test")
                .email("test@gmail.com")
                .className("SE")
                .build();

        when(repository.existsById(1L)).thenReturn(false);

        service.handleStudentCreated(event);

        verify(repository).save(any(StudentAnalytics.class));
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void handleStudentCreated_alreadyExists() {
        StudentCreatedEvent event = StudentCreatedEvent.builder()
                .studentId(1L)
                .build();

        when(repository.existsById(1L)).thenReturn(true);

        service.handleStudentCreated(event);

        verify(repository, never()).save(any());
    }

    // ===================== DELETE =====================
    @Test
    void handleStudentDeleted_success() {
        StudentDeletedEvent event = new StudentDeletedEvent(1L);

        service.handleStudentDeleted(event);

        verify(gradeCacheRepository).deleteByStudentId(1L);
        verify(studentCourseRepository).deleteByStudentId(1L);
        verify(repository).deleteById(1L);
    }

    // ===================== UPDATE =====================
    @Test
    void handleStudentUpdated_success() {
        StudentAnalytics analytics = StudentAnalytics.builder()
                .studentId(1L)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(analytics));

        StudentUpdatedEvent event = StudentUpdatedEvent.builder()
                .studentId(1L)
                .studentCode("NEW")
                .fullName("Updated")
                .email("new@gmail.com")
                .className("SE2")
                .build();

        service.handleStudentUpdated(event);

        assertEquals("NEW", analytics.getStudentCode());
        verify(repository).save(analytics);
    }

    // ===================== GRADE =====================
    @Test
    void handleGradeCreated_invalidData() {
        GradeCreatedEvent event = GradeCreatedEvent.builder()
                .studentId(1L)
                .build();

        assertThrows(BaseException.class,
                () -> service.handleGradeCreated(event));
    }

    @Test
    void handleGradeCreated_studentNotExists() {
        GradeCreatedEvent event = GradeCreatedEvent.builder()
                .studentId(1L)
                .score(8.0)
                .weight(0.5)
                .build();

        when(repository.existsById(1L)).thenReturn(false);

        service.handleGradeCreated(event);

        verify(gradeCacheRepository, never()).save(any());
    }

    @Test
    void handleGradeCreated_success() {
        GradeCreatedEvent event = GradeCreatedEvent.builder()
                .gradeId(1L)
                .studentId(1L)
                .subjectId(1L)
                .score(8.0)
                .weight(1.0)
                .credit(3)
                .build();

        when(repository.existsById(1L)).thenReturn(true);

        when(gradeCacheRepository.findByGradeId(1L))
                .thenReturn(Optional.empty());

        when(gradeCacheRepository.findByStudentIdAndSubjectId(1L, 1L))
                .thenReturn(List.of(
                        GradeCache.builder().score(8.0).weight(1.0).build()
                ));

        when(studentCourseRepository.findByStudentIdAndSubjectId(1L, 1L))
                .thenReturn(Optional.empty());

        when(studentCourseRepository.findByStudentId(1L))
                .thenReturn(List.of(
                        StudentCourse.builder()
                                .finalScore(8.0)
                                .credit(3)
                                .build()
                ));

        StudentAnalytics analytics = StudentAnalytics.builder()
                .studentId(1L)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(analytics));

        service.handleGradeCreated(event);

        verify(gradeCacheRepository).save(any());
        verify(studentCourseRepository).save(any());
        verify(repository).save(any());
        verify(eventPublisher).publishEvent(any());
    }

    // ===================== GET =====================
    @Test
    void getStudentAnalytics_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BaseException.class,
                () -> service.getStudentAnalytics(1L));
    }

    @Test
    void getStudentAnalytics_success() {
        StudentAnalytics analytics = StudentAnalytics.builder()
                .studentId(1L)
                .gpa(8.0)
                .totalSubjects(3)
                .classification(Classification.GOOD)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(analytics));

        var result = service.getStudentAnalytics(1L);

        assertEquals(1L, result.getStudentId());
        assertEquals(8.0, result.getGpa());
    }
}
