package com.stuman.analytics_service.repository;

import com.stuman.analytics_service.entity.GradeCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeCacheRepository extends JpaRepository<GradeCache, Long> {

    List<GradeCache> findByStudentIdAndSubjectId(Long studentId, Long subjectId);
    void deleteByStudentId(Long studentId);
    Optional<GradeCache> findByGradeId(Long gradeId);
}