package com.educore.auth.service;

import com.educore.auth.dto.response.UserInfoResponse;

public interface UserService {

    UserInfoResponse getUserInfo(Long userId);
}