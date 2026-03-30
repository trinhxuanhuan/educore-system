package com.stuman.analytics_service.service;

import com.stuman.analytics_service.dto.StudentAnalyticsResponse;
import com.stuman.common_event.GradeCreatedEvent;
import com.stuman.common_event.StudentCreatedEvent;
import com.stuman.common_event.StudentDeletedEvent;
import com.stuman.common_event.StudentUpdatedEvent;

import java.util.List;

public interface AnalyticsService {

    // ===== GET =====
    List<StudentAnalyticsResponse> getAllStudentAnalytics();

    StudentAnalyticsResponse getStudentAnalytics(Long studentId);

    List<StudentAnalyticsResponse> getTopStudents(int limit);

    // ===== HANDLE EVENT =====
    void handleStudentCreated(StudentCreatedEvent event);

    void handleGradeCreated(GradeCreatedEvent event);

    void handleStudentDeleted(StudentDeletedEvent event);

    void handleStudentUpdated(StudentUpdatedEvent event);
}