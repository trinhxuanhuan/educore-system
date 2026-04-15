package com.educore.grade.dto.request;

import com.educore.grade.entity.GradeType;
import com.educore.grade.entity.Semester;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateGradeRequest {

    @NotNull(message = "StudentId must not be null")
    private Long studentId;

    @NotNull(message = "SubjectId must not be null")
    private Long subjectId;

    @NotNull(message = "Grade type must not be null")
    private GradeType type;

    @NotNull(message = "Score must not be null")
    @DecimalMin(value = "0.0", message = "Score must be >= 0")
    @DecimalMax(value = "10.0", message = "Score must be <= 10")
    private Double score;

    @NotNull(message = "Weight must not be null")
    @Positive(message = "Weight must be greater than 0")
    private Double weight;

    @NotNull(message = "Semester must not be null")
    private Semester semester;

    @NotNull(message = "Academic year must not be null")
    @Min(value = 2000, message = "Academic year must be >= 2000")
    @Max(value = 2100, message = "Academic year must be <= 2100")
    private Integer academicYear;
}