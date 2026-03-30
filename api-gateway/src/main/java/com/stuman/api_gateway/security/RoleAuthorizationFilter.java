package com.stuman.api_gateway.security;

import com.stuman.api_gateway.exception.ForbiddenHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RoleAuthorizationFilter {

    private final ForbiddenHandler forbiddenHandler;

    public Mono<Void> authorize(ServerWebExchange exchange, String requiredRole) {

        String role = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Role");

        if (role == null || !role.equals(requiredRole)) {
            return forbiddenHandler.handle(exchange, "Access denied: " + requiredRole + " only");
        }

        return Mono.empty();
    }
}