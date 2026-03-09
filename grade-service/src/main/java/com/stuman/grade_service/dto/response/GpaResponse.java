package com.stuman.grade_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GpaResponse {

    private Long studentId;
    private String semester;
    private Integer academicYear;
    private Double gpa;
}
