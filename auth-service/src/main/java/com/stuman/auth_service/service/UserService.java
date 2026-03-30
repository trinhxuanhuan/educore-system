package com.stuman.auth_service.service;

import com.stuman.auth_service.dto.response.UserInfoResponse;

public interface UserService {

    UserInfoResponse getUserInfo(Long userId);
}