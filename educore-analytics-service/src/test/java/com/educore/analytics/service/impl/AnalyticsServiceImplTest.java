package com.educore.analytics.service.impl;

import com.educore.analytics.entity.Classification;
import com.educore.analytics.entity.GradeCache;
import com.educore.analytics.entity.StudentAnalytics;
import com.educore.analytics.entity.StudentCourse;
import com.educore.common.GradeCreatedEvent;
import com.educore.common.StudentCreatedEvent;
import com.educore.common.StudentDeletedEvent;
import com.educore.common.StudentUpdatedEvent;
import com.educore.analytics.entity.*;
import com.educore.analytics.exception.BaseException;
import com.educore.analytics.repository.GradeCacheRepository;
import com.educore.analytics.repository.StudentAnalyticsRepository;
import com.educore.analytics.repository.StudentCourseRepository;
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

    @BeforeEach
    void setup() {
        lenient().when(repository.save(any()))
                .thenAnswer(i -> i.getArguments()[0]);

        lenient().when(studentCourseRepository.save(any()))
                .thenAnswer(i -> i.getArguments()[0]);

        lenient().when(gradeCacheRepository.save(any()))
                .thenAnswer(i -> i.getArguments()[0]);
    }

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

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(captor.capture());

        Object publishedEvent = captor.getValue();
        assertTrue(publishedEvent instanceof AnalyticsServiceImpl.StudentAnalyticsCreatedEvent);
        var ev = (AnalyticsServiceImpl.StudentAnalyticsCreatedEvent) publishedEvent;
        assertEquals(1L, ev.studentId());
    }

    @Test
    void handleStudentCreated_alreadyExists() {
        when(repository.existsById(1L)).thenReturn(true);

        service.handleStudentCreated(
                StudentCreatedEvent.builder().studentId(1L).build()
        );

        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    // ===================== DELETE =====================

    @Test
    void handleStudentDeleted_success() {
        service.handleStudentDeleted(new StudentDeletedEvent(1L));

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
        assertEquals("Updated", analytics.getFullName());

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
        verify(studentCourseRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void handleGradeCreated_success() {
        Long studentId = 1L;
        Long subjectId = 1L;

        GradeCreatedEvent event = GradeCreatedEvent.builder()
                .gradeId(1L)
                .studentId(studentId)
                .subjectId(subjectId)
                .score(8.0)
                .weight(1.0)
                .credit(3)
                .build();

        when(repository.existsById(studentId)).thenReturn(true);

        when(gradeCacheRepository.findByGradeId(1L))
                .thenReturn(Optional.empty());

        when(gradeCacheRepository.findByStudentIdAndSubjectId(studentId, subjectId))
                .thenReturn(List.of(
                        GradeCache.builder()
                                .score(8.0)
                                .weight(1.0)
                                .build()
                ));

        when(studentCourseRepository.findByStudentIdAndSubjectId(studentId, subjectId))
                .thenReturn(Optional.empty());

        StudentCourse course = StudentCourse.builder()
                .studentId(studentId)
                .subjectId(subjectId)
                .finalScore(8.0)
                .credit(3)
                .build();

        when(studentCourseRepository.findByStudentId(studentId))
                .thenReturn(List.of(course));

        StudentAnalytics analytics = StudentAnalytics.builder()
                .studentId(studentId)
                .build();

        when(repository.findById(studentId))
                .thenReturn(Optional.of(analytics));

        service.handleGradeCreated(event);

        verify(gradeCacheRepository).save(any());
        verify(studentCourseRepository).save(any());

        verify(repository).save(argThat(a ->
                a.getGpa() != null &&
                        a.getTotalSubjects() == 1 &&
                        a.getTotalCredits() == 3 &&
                        a.getClassification() == Classification.GOOD
        ));

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(captor.capture());

        Object publishedEvent = captor.getValue();
        assertTrue(publishedEvent instanceof AnalyticsServiceImpl.StudentAnalyticsUpdatedEvent);
        var ev = (AnalyticsServiceImpl.StudentAnalyticsUpdatedEvent) publishedEvent;
        assertEquals(1L, ev.studentId());
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
        assertEquals("GOOD", result.getClassification().replace(" ", "_"));
    }
}
