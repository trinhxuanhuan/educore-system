package com.stuman.auth_service.controller;

import com.stuman.auth_service.dto.response.UserInfoResponse;
import com.stuman.auth_service.entity.User;
import com.stuman.auth_service.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public UserInfoResponse me(Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
                        .stream()
                        .map(role -> role.getName().name()) // ROLE enum -> String
                        .collect(Collectors.toSet())
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-only")
    public String adminOnly() {
        return "Hello ADMIN!";
    }
}
