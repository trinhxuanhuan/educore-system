package com.stuman.auth_service.service;

import com.stuman.auth_service.dto.request.LoginRequest;
import com.stuman.auth_service.dto.request.RegisterRequest;
import com.stuman.auth_service.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}