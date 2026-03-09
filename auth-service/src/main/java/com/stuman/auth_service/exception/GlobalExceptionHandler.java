package com.stuman.auth_service.exception;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
            BaseException ex,
            HttpServletRequest request
    ) {

        ErrorCode errorCode = ex.getErrorCode();

        ErrorResponse response = new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        String message = ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        ErrorResponse response = new ErrorResponse(
                ErrorCode.VALIDATION_ERROR.getCode(),
                message,
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(ErrorCode.VALIDATION_ERROR.getStatus())
                .body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request
    ) {

        ErrorResponse response = new ErrorResponse(
                ErrorCode.INTERNAL_ERROR.getCode(),
                ErrorCode.INTERNAL_ERROR.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(ErrorCode.INTERNAL_ERROR.getStatus())
                .body(response);
    }
}

