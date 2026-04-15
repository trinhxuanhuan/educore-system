package com.educore.grade.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentInternalResponse {

    private Long id;
    private String fullName;
    private Boolean active;

}
