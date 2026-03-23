package com.stuman.common_event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCreatedEvent {

    private Long studentId;
    private String studentCode;
    private String fullName;
    private String email;
    private String className;
}
