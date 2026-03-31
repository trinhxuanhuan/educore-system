package com.stuman.student_service.exception;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum ErrorCode {

    STUDENT_NOT_FOUND(
            "STUDENT_404",
            "Student not found",
            HttpStatus.NOT_FOUND
    ),

    STUDENT_ALREADY_EXISTS(
            "STUDENT_409",
            "Student already exists",
            HttpStatus.CONFLICT
    ),

    EMAIL_ALREADY_EXISTS(
            "STUDENT_409_EMAIL",
            "Email already exists",
            HttpStatus.CONFLICT
    ),

    STUDENT_CODE_ALREADY_EXISTS(
            "STUDENT_409_CODE",
            "Student code already exists",
            HttpStatus.CONFLICT
    ),

    VALIDATION_ERROR(
            "COMMON_400_VALIDATION",
            "Validation failed",
            HttpStatus.BAD_REQUEST
    ),

    //Không đủ quyền truy cập
    ACCESS_DENIED(
            "COMMON_403",
            "Access denied",
            HttpStatus.FORBIDDEN
    ),
    INVALID_REQUEST(
            "COMMON_400",
            "Invalid request",
            HttpStatus.BAD_REQUEST
    ),
    AUTH_SERVICE_UNAVAILABLE(
            "AUTH_503",
            "Auth service is unavailable",
            HttpStatus.SERVICE_UNAVAILABLE
    ),

    AUTH_USER_NOT_FOUND(
            "AUTH_404",
            "User not found in auth service",
            HttpStatus.NOT_FOUND
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
