package com.stuman.student_service.dto.request;

import com.stuman.student_service.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCreateRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @Pattern(
            regexp = "^(0[0-9]{9})$",
            message = "Phone number must be 10 digits and start with 0"
    )
    private String phoneNumber;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotBlank(message = "Class name is required")
    private String className;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;
}

