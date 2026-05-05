package com.educore.gateway.security;

import com.educore.gateway.exception.UnauthorizedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;
    private final RouteValidator routeValidator;
    private final UnauthorizedHandler unauthorizedHandler;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Public API → bỏ qua
        if (!routeValidator.isSecured.test(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorizedHandler.handle(exchange, "Missing Authorization header");
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            return unauthorizedHandler.handle(exchange, "Invalid JWT token");
        }

        Long userId = jwtService.getUserId(token);
        List<String> roles = jwtService.getRoles(token);

        if (userId == null || roles.isEmpty()) {
            return unauthorizedHandler.handle(exchange, "Invalid JWT payload");
        }

        String rolesHeader = String.join(",", roles);

        ServerWebExchange mutated = exchange.mutate()
                .request(builder -> builder
                        .header("X-User-Id", String.valueOf(userId))
                        .header("X-Roles", rolesHeader)
                        .header(HttpHeaders.AUTHORIZATION, authHeader) // optional but GOOD PRACTICE
                )
                .build();

        return chain.filter(mutated);
    }

    @Override
    public int getOrder() {
        return -1; // luôn chạy trước
    }
}