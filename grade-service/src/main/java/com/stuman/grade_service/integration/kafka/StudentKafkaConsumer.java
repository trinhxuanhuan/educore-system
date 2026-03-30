package com.stuman.grade_service.integration.kafka;

import com.stuman.common_event.StudentDeletedEvent;
import com.stuman.grade_service.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentKafkaConsumer {

    private final GradeRepository gradeRepository;

    @KafkaListener(topics = "student.deleted", groupId = "grade-group")
    public void handleStudentDeleted(StudentDeletedEvent event) {

        Long studentId = event.getStudentId();

        gradeRepository.deleteByStudentId(studentId);

        log.info("Deleted all grades of studentId = {}", studentId);
    }
}
