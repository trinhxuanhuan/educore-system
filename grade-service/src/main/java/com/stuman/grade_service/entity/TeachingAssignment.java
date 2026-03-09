package com.stuman.grade_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teaching_assignment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"teacher_id", "subject_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeachingAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;  // userId từ JWT

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;
}
