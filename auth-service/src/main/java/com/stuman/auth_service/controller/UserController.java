package com.stuman.auth_service.controller;

import com.stuman.auth_service.dto.response.ApiResponse;
import com.stuman.auth_service.dto.response.UserInfoResponse;
import com.stuman.auth_service.entity.User;
import com.stuman.auth_service.mapper.UserMapper;
import com.stuman.auth_service.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
@RequiredArgsConstructor
@Tag(name = "User API", description = "User operations")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Operation(
            summary = "Get current user information",
            description = "Retrieve profile information of the currently authenticated user"
    )
    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> me(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserInfoResponse userInfo = userMapper.toUserInfo(user);

        return ApiResponse.<UserInfoResponse>builder()
                .code("SUCCESS")
                .message("Get user info success")
                .data(userInfo)
                .build();
    }

    @Operation(
            summary = "Admin-only endpoint",
            description = "Accessible only by users with ADMIN role"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-only")
    public String adminOnly() {
        return "Hello ADMIN!";
    }
}
