package com.educore.grade.integration.feign;

import com.educore.grade.dto.response.UserInfoResponse;
import com.educore.grade.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "auth-service",
        configuration = FeignConfig.class
)
public interface AuthClient {

    @GetMapping("/internal/users/{userId}")
    UserInfoResponse getUserById(
            @PathVariable("userId") Long userId
    );
}
