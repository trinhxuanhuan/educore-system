package com.stuman.grade_service.repository;

import com.stuman.grade_service.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findByCode(String code);
    boolean existsByCode(String code);
}
