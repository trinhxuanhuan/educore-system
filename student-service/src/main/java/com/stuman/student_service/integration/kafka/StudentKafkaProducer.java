package com.stuman.student_service.integration.kafka;
import com.stuman.common_event.StudentCreatedEvent;
import com.stuman.common_event.StudentDeletedEvent;
import com.stuman.common_event.StudentUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendStudentCreatedEvent(StudentCreatedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.STUDENT_CREATED,
                event.getStudentId().toString(),
                event
        );
    }
    public void sendStudentDeletedEvent(StudentDeletedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.STUDENT_DELETED,
                event.getStudentId().toString(),
                event
        );
    }

    public void sendStudentUpdatedEvent(StudentUpdatedEvent event) {
        kafkaTemplate.send(
                KafkaTopics.STUDENT_UPDATED,
                event.getStudentId().toString(),
                event
        );
    }
}
