package com.stuman.grade_service.integration.feign;

import com.stuman.grade_service.dto.response.UserInfoResponse;
import com.stuman.grade_service.config.FeignConfig;
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
