package com.stuman.grade_service.integration;

import com.stuman.grade_service.config.FeignConfig;
import com.stuman.grade_service.dto.response.StudentInternalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "student-service",
        configuration = FeignConfig.class
)
public interface StudentClient {

    @GetMapping("/internal/students/{id}")
    StudentInternalResponse getStudentById(
            @PathVariable("id") Long id
    );
}