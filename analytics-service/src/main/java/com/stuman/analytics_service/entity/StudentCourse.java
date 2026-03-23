package com.stuman.analytics_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private Long subjectId;

    private Double finalScore;

    private Integer credit;
}
