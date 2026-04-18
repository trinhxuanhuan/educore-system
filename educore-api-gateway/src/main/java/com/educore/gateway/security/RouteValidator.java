package com.educore.gateway.security;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/v1/auth",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-resources",
            "/webjars",
            "/eureka"
    );

    public Predicate<String> isSecured =
            path -> PUBLIC_ENDPOINTS.stream()
                    .noneMatch(path::startsWith);
}