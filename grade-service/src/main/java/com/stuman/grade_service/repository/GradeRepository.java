package com.stuman.grade_service.repository;

import com.stuman.grade_service.dto.response.GradeResponse;
import com.stuman.grade_service.entity.Grade;
import com.stuman.grade_service.entity.Semester;
import org.springframework.data.domain.Page;
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
}