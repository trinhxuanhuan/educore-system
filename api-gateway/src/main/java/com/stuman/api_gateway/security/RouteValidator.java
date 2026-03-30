package com.stuman.api_gateway.security;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/webjars",
            "/swagger-resources",
            "/eureka"
    );

    public Predicate<String> isSecured =
            path -> PUBLIC_ENDPOINTS.stream()
                    .noneMatch(publicPath ->
                            path.equals(publicPath) || path.startsWith(publicPath + "/")
                    );
}