package com.educore.auth.controller;

import com.educore.auth.dto.request.ChangePasswordRequest;
import com.educore.auth.dto.request.LoginRequest;
import com.educore.auth.dto.request.RegisterRequest;
import com.educore.auth.dto.response.ApiResponse;
import com.educore.auth.dto.response.AuthResponse;
import com.educore.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Auth API", description = "Authentication operations")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register new user")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .code("SUCCESS")
                        .message("Register successful")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Login user and return JWT")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .code("SUCCESS")
                        .message("Login successful")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Change password of the currently authenticated user")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequest request) {

        authService.changePassword(principal.getName(), request);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code("SUCCESS")
                        .message("Password changed successfully")
                        .build()
        );
    }
}
