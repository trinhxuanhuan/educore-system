package com.educore.common;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeCreatedEvent {

    private Long gradeId;

    private Long studentId;
    private Long subjectId;

    private String type;

    private Double score;
    private Double weight;

    private Integer credit;

    private String semester;
    private Integer academicYear;
}