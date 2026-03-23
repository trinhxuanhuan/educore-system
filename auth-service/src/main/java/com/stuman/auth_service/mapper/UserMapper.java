package com.stuman.auth_service.mapper;

import com.stuman.auth_service.dto.response.UserInfoResponse;
import com.stuman.auth_service.entity.Role;
import com.stuman.auth_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles")
    UserInfoResponse toUserInfo(User user);

    // Map Role -> String
    default String map(Role role) {
        return role.getName().name();
    }
}
