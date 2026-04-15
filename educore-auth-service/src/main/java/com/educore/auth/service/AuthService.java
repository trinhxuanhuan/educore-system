package com.educore.auth.service;

import com.educore.auth.dto.request.LoginRequest;
import com.educore.auth.dto.request.RegisterRequest;
import com.educore.auth.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}