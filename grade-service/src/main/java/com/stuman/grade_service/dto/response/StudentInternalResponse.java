package com.stuman.grade_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentInternalResponse {

    private Long id;
    private String fullName;
    private Boolean active;
}
