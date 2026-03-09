package com.stuman.auth_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthUserResponse {
    private String userId;
    private String username;
    private String email;
}

