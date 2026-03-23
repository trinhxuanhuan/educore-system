package com.stuman.analytics_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnalytics {

    @Id
    private Long studentId;

    private String studentCode;

    private String fullName;

    private String email;

    private String className;

    private Double gpa;

    private Integer totalSubjects;

    @Enumerated(EnumType.STRING)
    @Column(name = "classification")
    private Classification classification;
    private Integer totalCredits;
    private Double averageScore;

}