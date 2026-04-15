package com.educore.analytics.exception;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum ErrorCode {

    // ===== ANALYTICS =====
    STUDENT_ANALYTICS_NOT_FOUND(
            "ANALYTICS_404",
            "Student analytics not found",
            HttpStatus.NOT_FOUND
    ),

    ANALYTICS_ALREADY_EXISTS(
            "ANALYTICS_409",
            "Analytics already exists",
            HttpStatus.CONFLICT
    ),

    // ===== COMMON =====
    INVALID_REQUEST(
            "COMMON_400",
            "Invalid request",
            HttpStatus.BAD_REQUEST
    ),

    // ===== VALIDATION =====
    INVALID_GRADE_DATA(
            "GRADE_400",
            "Invalid grade data (score or weight is null)",
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