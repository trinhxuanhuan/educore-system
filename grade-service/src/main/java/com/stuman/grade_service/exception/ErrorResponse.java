package com.stuman.grade_service.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String errorCode;
    private String message;
    private String path;

    private Map<String, String> validationErrors;
}