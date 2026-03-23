package com.stuman.grade_service.integration.kafka;
import com.stuman.common_event.GradeCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GradeKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendGradeCreatedEvent(GradeCreatedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.GRADE_CREATED,
                event.getStudentId().toString(),
                event
        );
    }
}