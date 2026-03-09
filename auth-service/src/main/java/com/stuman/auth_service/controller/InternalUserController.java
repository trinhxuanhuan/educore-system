package com.stuman.auth_service.controller;
import com.stuman.auth_service.exception.BaseException;
import com.stuman.auth_service.exception.ErrorCode;
import com.stuman.auth_service.dto.response.UserInfoResponse;
import com.stuman.auth_service.entity.User;
import com.stuman.auth_service.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final UserRepository userRepository;

    public InternalUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public UserInfoResponse getUserById(
            @PathVariable Long userId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(
                        user.getRoles()
                                .stream()
                                .map(role -> role.getName().name())
                                .collect(Collectors.toSet())
                )
                .build();
    }
}