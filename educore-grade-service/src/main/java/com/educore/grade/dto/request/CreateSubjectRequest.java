package com.educore.grade.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateSubjectRequest {

    @NotBlank(message = "Subject code must not be blank")
    @Size(max = 20, message = "Subject code must not exceed 20 characters")
    private String code;

    @NotBlank(message = "Subject name must not be blank")
    @Size(max = 100, message = "Subject name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Credit must not be null")
    @Min(value = 1, message = "Credit must be at least 1")
    @Max(value = 10, message = "Credit must not exceed 10")
    private Integer credit;
}