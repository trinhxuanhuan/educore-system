package com.stuman.grade_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long subjectId;

    @Enumerated(EnumType.STRING)
    private GradeType type;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false)
    private Double weight;

    @Enumerated(EnumType.STRING)
    private Semester semester;

    @Column(nullable = false)
    private Integer academicYear;

    private Long createdBy;
    private Long updatedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}