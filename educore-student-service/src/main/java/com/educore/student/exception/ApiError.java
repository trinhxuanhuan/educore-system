package com.educore.student.exception;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {

    private String code;
    private String message;
    private int status;
    private String path;
    private LocalDateTime timestamp;

    // dành riêng cho validation
    private Map<String, String> errors;
}


