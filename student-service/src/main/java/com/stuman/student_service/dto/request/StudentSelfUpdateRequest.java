package com.stuman.student_service.dto.request;

import com.stuman.student_service.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSelfUpdateRequest {

    @Email(message = "Invalid email format")
    @Size(max = 100)
    private String email;

    @Size(max = 100)
    private String fullName;

    @Past
    private LocalDate dateOfBirth;

    private Gender gender;

    @Pattern(
            regexp = "^(0[0-9]{9})$",
            message = "Phone number must be 10 digits and start with 0"
    )
    private String phoneNumber;

    @Size(max = 255)
    private String address;
}
