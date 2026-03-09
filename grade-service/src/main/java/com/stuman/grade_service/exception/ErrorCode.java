package com.stuman.grade_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // ===== GRADE =====
    GRADE_NOT_FOUND(
            "GRADE_404",
            "Grade not found",
            HttpStatus.NOT_FOUND
    ),

    GRADE_ALREADY_EXISTS(
            "GRADE_409",
            "Grade already exists",
            HttpStatus.CONFLICT
    ),


    // ===== SUBJECT =====
    SUBJECT_NOT_FOUND(
            "SUBJECT_404",
            "Subject not found",
            HttpStatus.NOT_FOUND
    ),
    // ===== ASSIGNMENT =====
    ASSIGNMENT_ALREADY_EXISTS(
            "ASSIGNMENT_409",
            "Teacher already assigned to this subject",
            HttpStatus.CONFLICT
    ),

    SUBJECT_CODE_ALREADY_EXISTS(
            "SUBJECT_409_CODE",
            "Subject code already exists",
            HttpStatus.CONFLICT
    ),

    // ===== STUDENT =====
    STUDENT_NOT_FOUND(
            "STUDENT_404",
            "Student not found",
            HttpStatus.NOT_FOUND
    ),

    // ===== TEACHER =====
    INVALID_TEACHER(
            "TEACHER_403",
            "User is not a valid teacher",
            HttpStatus.FORBIDDEN
    ),

    // ===== COMMON =====
    VALIDATION_ERROR(
            "COMMON_400_VALIDATION",
            "Validation failed",
            HttpStatus.BAD_REQUEST
    ),

    ACCESS_DENIED(
            "COMMON_403",
            "Access denied",
            HttpStatus.FORBIDDEN
    ),
    // ===== ASSIGNMENT =====
    TEACHER_NOT_ASSIGNED(
            "ASSIGNMENT_403",
            "Teacher is not assigned to this subject",
            HttpStatus.FORBIDDEN
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