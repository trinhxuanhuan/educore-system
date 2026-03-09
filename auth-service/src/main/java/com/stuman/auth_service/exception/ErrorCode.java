package com.stuman.auth_service.exception;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // ===== USER =====
    USER_NOT_FOUND(
            "USER_404",
            "User not found",
            HttpStatus.NOT_FOUND
    ),

    USER_ALREADY_EXISTS(
            "USER_409",
            "User already exists",
            HttpStatus.CONFLICT
    ),

    // ===== ROLE =====
    ROLE_NOT_FOUND(
            "ROLE_404",
            "Role not found",
            HttpStatus.NOT_FOUND
    ),

    INVALID_ROLE(
            "ROLE_400",
            "Invalid role",
            HttpStatus.BAD_REQUEST
    ),

    // ===== AUTH =====
    INVALID_CREDENTIALS(
            "AUTH_401",
            "Invalid username or password",
            HttpStatus.UNAUTHORIZED
    ),

    ACCESS_DENIED(
            "AUTH_403",
            "Access denied",
            HttpStatus.FORBIDDEN
    ),

    // ===== COMMON =====
    VALIDATION_ERROR(
            "COMMON_400_VALIDATION",
            "Validation failed",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_REQUEST(
            "COMMON_400",
            "Invalid request",
            HttpStatus.BAD_REQUEST
    ),

    INTERNAL_ERROR(
            "COMMON_500",
            "Internal server error",
            HttpStatus.INTERNAL_SERVER_ERROR
    );

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
