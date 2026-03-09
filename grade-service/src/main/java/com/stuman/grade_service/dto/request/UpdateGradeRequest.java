package com.stuman.grade_service.dto.request;

import com.stuman.grade_service.entity.Semester;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGradeRequest {

    @DecimalMin(value = "0.0", message = "Score must be >= 0")
    @DecimalMax(value = "10.0", message = "Score must be <= 10")
    private Double score;

    @Positive(message = "Weight must be greater than 0")
    private Double weight;

    private Semester semester;

    @Min(value = 2000, message = "Academic year must be >= 2000")
    @Max(value = 2100, message = "Academic year must be <= 2100")
    private Integer academicYear;
}