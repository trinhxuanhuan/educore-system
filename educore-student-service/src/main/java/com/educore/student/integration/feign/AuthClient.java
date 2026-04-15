package com.educore.student.integration.feign;

import com.educore.student.config.FeignConfig;
import com.educore.student.dto.response.AuthUserResponse;
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