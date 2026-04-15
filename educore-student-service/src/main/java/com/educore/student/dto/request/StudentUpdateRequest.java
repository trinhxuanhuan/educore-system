package com.educore.student.dto.request;

import com.educore.student.entity.Gender;
import com.educore.student.entity.StudentStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentUpdateRequest {

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 20, message = "Student code must not exceed 20 characters")
    private String studentCode;

    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Pattern(
            regexp = "^(0[0-9]{9})$",
            message = "Phone number must be 10 digits and start with 0"
    )
    private String phoneNumber;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Size(max = 50, message = "Class name must not exceed 50 characters")
    private String className;

    private StudentStatus status;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;
}

