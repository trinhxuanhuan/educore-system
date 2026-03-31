package com.stuman.student_service.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSummaryResponse {
    private Long id;
    private String studentCode;
    private String fullName;
    private String email;
    private String className;
    private String status;
}

