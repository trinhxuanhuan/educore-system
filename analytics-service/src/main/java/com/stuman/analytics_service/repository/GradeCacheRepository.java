package com.stuman.analytics_service.repository;

import com.stuman.analytics_service.entity.GradeCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GradeCacheRepository extends JpaRepository<GradeCache, Long> {

    List<GradeCache> findByStudentIdAndSubjectId(Long studentId, Long subjectId);
}