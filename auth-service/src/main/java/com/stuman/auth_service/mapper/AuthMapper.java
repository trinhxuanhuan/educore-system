package com.stuman.auth_service.mapper;

import com.stuman.auth_service.dto.response.AuthResponse;
import com.stuman.auth_service.dto.response.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AuthMapper {

    @Mapping(target = "accessToken", source = "token")
    @Mapping(target = "user", source = "user")
    AuthResponse toAuthResponse(String token, UserInfoResponse user);
}
