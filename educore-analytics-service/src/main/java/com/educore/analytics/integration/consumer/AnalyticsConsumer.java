package com.educore.analytics.integration.consumer;



import com.educore.analytics.integration.topic.KafkaTopics;
import com.educore.analytics.service.AnalyticsService;
import com.educore.common.GradeCreatedEvent;
import com.educore.common.StudentCreatedEvent;
import com.educore.common.StudentDeletedEvent;
import com.educore.common.StudentUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(topics = KafkaTopics.STUDENT_CREATED, groupId = "analytics-group")
    public void consumeStudent(StudentCreatedEvent event) {
        log.info("RECEIVED STUDENT EVENT: {}", event);
        analyticsService.handleStudentCreated(event);
    }
    @KafkaListener(topics = KafkaTopics.STUDENT_DELETED, groupId = "analytics-group")
    public void consumeStudentDeleted(StudentDeletedEvent event) {
        log.info("RECEIVED STUDENT DELETE EVENT: {}", event);
        analyticsService.handleStudentDeleted(event);
    }

    @KafkaListener(topics = KafkaTopics.GRADE_CREATED, groupId = "analytics-group")
    public void consumeGrade(GradeCreatedEvent event) {
        log.info("RECEIVED GRADE EVENT: {}", event);
        analyticsService.handleGradeCreated(event);
    }

    @KafkaListener(topics = KafkaTopics.STUDENT_UPDATED, groupId = "analytics-group")
    public void consumeStudentUpdated(StudentUpdatedEvent event) {

        log.info("RECEIVED STUDENT UPDATED EVENT: {}", event);

        analyticsService.handleStudentUpdated(event);
    }
}