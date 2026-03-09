package com.stuman.student_service.dto.response;
import lombok.Data;

@Data
public class AuthUserResponse {

    private String userId;
    private String email;
    private String username;
}

