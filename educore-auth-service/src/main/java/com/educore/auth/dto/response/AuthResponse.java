package com.educore.auth.dto.response;

public class AuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private UserInfoResponse user;
    private boolean passwordChangeRequired;

    public AuthResponse(String accessToken, UserInfoResponse user, boolean passwordChangeRequired) {
        this.accessToken = accessToken;
        this.user = user;
        this.passwordChangeRequired = passwordChangeRequired;
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

    public boolean isPasswordChangeRequired() {
        return passwordChangeRequired;
    }
}
