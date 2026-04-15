package com.educore.grade.integration.kafka;

import com.educore.common.StudentDeletedEvent;
import com.educore.grade.repository.GradeRepository;
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
