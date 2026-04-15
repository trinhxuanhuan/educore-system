package com.educore.grade.repository;

import com.educore.grade.entity.Grade;
import com.educore.grade.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GradeRepository
        extends JpaRepository<Grade, Long>,
        JpaSpecificationExecutor<Grade> {

    List<Grade> findByStudentId(Long studentId);

    List<Grade> findByStudentIdAndSemesterAndAcademicYear(
            Long studentId,
            Semester semester,
            Integer academicYear
    );
    void deleteByStudentId(Long studentId);
}