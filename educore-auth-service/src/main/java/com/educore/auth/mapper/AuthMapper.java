package com.educore.auth.mapper;

import com.educore.auth.dto.response.AuthResponse;
import com.educore.auth.dto.response.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AuthMapper {

    @Mapping(target = "accessToken", source = "token")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "passwordChangeRequired", source = "passwordChangeRequired")
    AuthResponse toAuthResponse(String token, UserInfoResponse user, boolean passwordChangeRequired);
}
