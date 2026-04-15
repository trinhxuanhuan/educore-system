package com.educore.student.security;

import lombok.*;

import java.util.Set;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomUserPrincipal {

    private Long userId;
    private String username;
    private Set<String> roles;

    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
