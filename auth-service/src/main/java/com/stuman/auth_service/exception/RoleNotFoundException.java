package com.stuman.auth_service.exception;

import org.springframework.http.HttpStatus;

public class RoleNotFoundException extends BusinessException {
    public RoleNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
