package com.educore.grade.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignTeacherRequest {

    @NotNull(message = "Teacher id must not be null")
    private Long teacherId;

    @NotNull(message = "Subject id must not be null")
    private Long subjectId;
}
