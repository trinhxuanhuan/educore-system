package com.stuman.student_service.exception;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
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


