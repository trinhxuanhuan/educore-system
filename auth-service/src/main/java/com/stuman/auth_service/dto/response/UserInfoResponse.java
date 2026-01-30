package com.stuman.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
@Getter
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
}


