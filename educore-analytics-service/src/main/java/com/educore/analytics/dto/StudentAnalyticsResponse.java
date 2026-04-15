package com.educore.analytics.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentAnalyticsResponse {

    private Long studentId;

    private Double gpa;

    private Integer totalSubjects;

    private String classification;


}