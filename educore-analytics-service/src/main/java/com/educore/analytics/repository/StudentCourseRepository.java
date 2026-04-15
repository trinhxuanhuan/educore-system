package com.educore.analytics.repository;

import com.educore.analytics.entity.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {

    Optional<StudentCourse> findByStudentIdAndSubjectId(Long studentId, Long subjectId);

    List<StudentCourse> findByStudentId(Long studentId);
    void deleteByStudentId(Long studentId);
}