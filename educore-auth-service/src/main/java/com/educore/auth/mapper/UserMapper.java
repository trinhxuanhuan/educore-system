package com.educore.auth.mapper;

import com.educore.auth.dto.response.UserInfoResponse;
import com.educore.auth.entity.Role;
import com.educore.auth.entity.User;
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
