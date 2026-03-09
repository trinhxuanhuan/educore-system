package com.stuman.grade_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectResponse {

    private Long id;
    private String code;
    private String name;
    private Integer credit;
    private String status;
}
