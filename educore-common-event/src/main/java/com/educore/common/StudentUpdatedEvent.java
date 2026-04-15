package com.educore.common;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdatedEvent {

    private Long studentId;
    private String studentCode;
    private String fullName;
    private String email;
    private String className;
}