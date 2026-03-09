package com.stuman.grade_service.repository;

import com.stuman.grade_service.entity.TeachingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeachingAssignmentRepository
        extends JpaRepository<TeachingAssignment, Long> {

    boolean existsByTeacherIdAndSubjectId(Long teacherId, Long subjectId);
    List<TeachingAssignment> findByTeacherId(Long teacherId);
}
