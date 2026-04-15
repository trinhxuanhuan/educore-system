package com.educore.analytics.service;

import com.educore.analytics.dto.StudentAnalyticsResponse;
import com.educore.common.GradeCreatedEvent;
import com.educore.common.StudentCreatedEvent;
import com.educore.common.StudentDeletedEvent;
import com.educore.common.StudentUpdatedEvent;

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