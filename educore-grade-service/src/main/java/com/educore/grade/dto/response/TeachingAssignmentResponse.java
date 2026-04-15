package com.educore.grade.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeachingAssignmentResponse {

    private Long teacherId;
    private Long subjectId;
}