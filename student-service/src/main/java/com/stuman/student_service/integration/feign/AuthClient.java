package com.stuman.student_service.integration.feign;

import com.stuman.student_service.config.FeignConfig;
import com.stuman.student_service.dto.response.AuthUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "auth-service",
        configuration = FeignConfig.class
)
public interface AuthClient {

    @GetMapping("/internal/users/{userId}")
    AuthUserResponse getUserById(@PathVariable("userId") Long userId);

}