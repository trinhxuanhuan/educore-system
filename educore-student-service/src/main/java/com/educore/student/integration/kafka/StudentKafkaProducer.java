package com.educore.student.integration.kafka;
import com.educore.common.StudentCreatedEvent;
import com.educore.common.StudentDeletedEvent;
import com.educore.common.StudentUpdatedEvent;
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
