package com.stuman.api_gateway.security;

import com.stuman.api_gateway.exception.UnauthorizedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;
    private final RouteValidator routeValidator;
    private final UnauthorizedHandler unauthorizedHandler;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        //Skip public APIs
        if (!routeValidator.isSecured.test(path)) {
            return chain.filter(exchange);
        }
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);
        //Không có token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorizedHandler.handle(exchange, "Missing Authorization header");
        }
        String token = authHeader.substring(7);
        // Token sai
        if (!jwtService.isTokenValid(token)) {
            return unauthorizedHandler.handle(exchange, "Invalid JWT token");
        }
        // Extract info từ JWT
        Long userId = jwtService.getUserId(token);
        String role = jwtService.getRole(token);
        //Token thiếu data
        if (userId == null || role == null) {
            return unauthorizedHandler.handle(exchange, "Invalid JWT payload");
        }
        //Forward xuống service
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder
                        .header("X-User-Id", String.valueOf(userId))
                        .header("X-Role", role)
                )
                .build();

        return chain.filter(mutatedExchange);
    }
    //QUAN TRỌNG: chạy TRƯỚC AuthorizationFilter
    @Override
    public int getOrder() {
        return 0;
    }
}