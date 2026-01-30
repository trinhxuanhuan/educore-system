package com.stuman.auth_service.dto.response;

public class AuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private UserInfoResponse user;

    public AuthResponse(String accessToken, UserInfoResponse user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public UserInfoResponse getUser() {
        return user;
    }
}

