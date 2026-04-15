package com.educore.grade.dto.response;

import com.educore.grade.entity.GradeType;
import com.educore.grade.entity.Semester;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GradeResponse {

    private Long id;
    private Long studentId;

    private Long subjectId;
    private String subjectName;

    private GradeType type;
    private Double score;
    private Double weight;

    private Semester semester;
    private Integer academicYear;
}