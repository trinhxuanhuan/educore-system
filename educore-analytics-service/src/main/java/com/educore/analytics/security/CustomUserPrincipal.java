package com.educore.analytics.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class CustomUserPrincipal implements UserDetails {

    private Long userId;
    private List<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    // ===== Helper =====
    private boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(role ->
                        role.equals(roleName) || role.equals("ROLE_" + roleName)
                );
    }

    // ===== Dùng =====
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean isTeacher() {
        return hasRole("TEACHER");
    }

    public boolean isStudent() {
        return hasRole("STUDENT");
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userId.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}