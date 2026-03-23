package com.stuman.analytics_service.integration.consumer;



import com.stuman.analytics_service.integration.topic.KafkaTopics;
import com.stuman.analytics_service.service.AnalyticsService;
import com.stuman.common_event.GradeCreatedEvent;
import com.stuman.common_event.StudentCreatedEvent;
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

    @KafkaListener(topics = KafkaTopics.GRADE_CREATED, groupId = "analytics-group")
    public void consumeGrade(GradeCreatedEvent event) {

        log.info("RECEIVED GRADE EVENT: {}", event);

        analyticsService.handleGradeCreated(event);
    }
}