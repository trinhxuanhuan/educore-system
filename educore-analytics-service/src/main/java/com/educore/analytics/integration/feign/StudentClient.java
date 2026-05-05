package com.educore.analytics.integration.feign;

import com.educore.analytics.config.FeignConfig;
import com.educore.analytics.dto.StudentInternalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "student-service",
        configuration = FeignConfig.class
)
public interface StudentClient {

    @GetMapping("/internal/students/user/{userId}")
    StudentInternalResponse getStudentByUserId(
            @PathVariable("userId") Long userId
    );
}
