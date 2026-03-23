package com.stuman.analytics_service.repository;

import com.stuman.analytics_service.entity.StudentAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface StudentAnalyticsRepository
        extends JpaRepository<StudentAnalytics, Long> {
    @Query(value = """
    SELECT 
        CASE 
            WHEN SUM(weight) = 0 THEN 0
            ELSE SUM(score * weight) / SUM(weight)
        END,
        COUNT(DISTINCT subject_id)
    FROM grades
    WHERE student_id = :studentId
""", nativeQuery = true)
    Object[] calculateAnalyticsByStudentId(@Param("studentId") Long studentId);
}