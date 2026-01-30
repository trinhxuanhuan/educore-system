package com.stuman.auth_service.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String username) {
        super("User not found: " + username, HttpStatus.NOT_FOUND);
    }
}
