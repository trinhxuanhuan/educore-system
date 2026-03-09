package com.stuman.student_service.repository;

import com.stuman.student_service.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByUserId(Long userId);

    boolean existsByEmail(String email);
    boolean existsByStudentCode(String studentCode);

    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByStudentCodeAndIdNot(String studentCode, Long id);
    Optional<Student> findTopByStudentCodeStartingWithOrderByStudentCodeDesc(String prefix);


    Optional<Student> findByUserId(Long userId);
}

