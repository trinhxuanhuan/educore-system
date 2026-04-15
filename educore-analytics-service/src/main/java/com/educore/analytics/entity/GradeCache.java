package com.educore.analytics.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "grade_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeCache {

    @Id
    private Long gradeId;

    private Long studentId;
    private Long subjectId;

    private Double score;
    private Double weight;
}
