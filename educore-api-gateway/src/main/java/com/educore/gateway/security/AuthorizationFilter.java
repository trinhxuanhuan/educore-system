package com.educore.gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;
@Component
@RequiredArgsConstructor
public class AuthorizationFilter implements WebFilter {
    private final JwtService jwtService;
    private final RouteValidator routeValidator;
    // Mapping path → allowed roles
    private static final Map<String, List<String>> ROLE_MAP = Map.of(
            "/api/v1/users", List.of("ADMIN"),
            "/api/v1/admin", List.of("ADMIN"),
            "/api/v1/students", List.of("ADMIN", "TEACHER"),
            "/api/v1/grades", List.of("TEACHER"),
            "/api/v1/subjects", List.of("TEACHER"),
            "/api/v1/assignments", List.of("TEACHER"),
            "/api/v1/analytics", List.of("ADMIN")
    );
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        // Nếu PUBLIC endpoint → bỏ qua
        if (!routeValidator.isSecured.test(path)) {
            return chain.filter(exchange);
        }
        // Lấy token từ header
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        // Lấy role user từ token
        String userRole = jwtService.getRole(token);
        // Kiểm tra quyền
        boolean authorized = ROLE_MAP.entrySet().stream()
                .filter(entry -> path.startsWith(entry.getKey()))
                .anyMatch(entry -> entry.getValue().contains(userRole));
        if (!authorized) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
}