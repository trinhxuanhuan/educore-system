package com.educore.student.repository;

import com.educore.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // SOFT DELETE SUPPORT
    Optional<Student> findByIdAndDeletedFalse(Long id);
    boolean existsByIdAndDeletedFalse(Long id);

    Optional<Student> findByUserIdAndDeletedFalse(Long userId);
    Page<Student> findAllByDeletedFalse(Pageable pageable);
}

