package com.educore.gateway.security;

import com.educore.gateway.exception.ForbiddenHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RoleAuthorizationFilter {

    private final ForbiddenHandler forbiddenHandler;

    public Mono<Void> authorize(String role, String requiredRole, ServerWebExchange exchange) {

        if (role == null || !role.equals(requiredRole)) {
            return forbiddenHandler.handle(exchange, "Access denied: " + requiredRole + " only");
        }

        return Mono.empty();
    }
}