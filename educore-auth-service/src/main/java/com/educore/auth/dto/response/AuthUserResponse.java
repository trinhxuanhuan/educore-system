package com.educore.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthUserResponse {
    private String userId;
    private String username;
    private String email;
}

