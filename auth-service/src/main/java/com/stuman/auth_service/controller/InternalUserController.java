package com.stuman.auth_service.controller;
import com.stuman.auth_service.dto.response.UserInfoResponse;
import com.stuman.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserInfoResponse getUserById(@PathVariable("userId") Long userId) {
        return userService.getUserInfo(userId);
    }
}