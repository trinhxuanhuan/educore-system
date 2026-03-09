package com.stuman.auth_service.exception;
import lombok.AllArgsConstructor;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String code;
    private String message;
    private String path;
    private LocalDateTime timestamp;
}
