package com.educore.grade.integration.feign;
import com.educore.grade.config.FeignConfig;
import com.educore.grade.dto.response.StudentInternalResponse;
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

    @GetMapping("/internal/students/user/{userId}")
    StudentInternalResponse getStudentByUserId(
            @PathVariable("userId") Long userId
    );
}