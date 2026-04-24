package com.educore.student.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class CustomUserPrincipal implements UserDetails {

    private Long userId;
    private String username;
    private Set<String> roles = new HashSet<>();

    public CustomUserPrincipal(Long userId, String username, Set<String> roles) {
        this.userId = userId;
        this.username = username;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
    }

    // Factory method (clean + safe null)
    public static CustomUserPrincipal fromJwt(
            Long userId,
            String username,
            List<String> roles
    ) {
        return new CustomUserPrincipal(
                userId,
                username,
                roles != null ? new HashSet<>(roles) : new HashSet<>()
        );
    }

    // ================= AUTHORITY =================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet()); //
    }

    // ================= ROLE CHECK =================

    // Check 1 role
    public boolean hasRole(String role) {
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return roles.contains(roleWithPrefix);
    }

    // Check nhiều role
    public boolean hasAnyRole(String... rolesToCheck) {
        if (rolesToCheck == null) return false;
        return Arrays.stream(rolesToCheck).anyMatch(this::hasRole);
    }

    // ================= USER DETAILS =================

    @Override
    public String getPassword() {
        return null; // JWT không dùng password
    }

    @Override
    public String getUsername() {
        return username;
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

    // ================= DEBUG (optional) =================
    @Override
    public String toString() {
        return "CustomUserPrincipal{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", roles=" + roles +
                '}';
    }
}