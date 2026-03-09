package com.stuman.grade_service.dto.response;

import com.stuman.grade_service.entity.GradeType;
import com.stuman.grade_service.entity.Semester;
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