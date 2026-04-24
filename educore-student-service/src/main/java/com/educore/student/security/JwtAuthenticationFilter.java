package com.educore.student.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);

                if (jwtProvider.validateToken(token)) {

                    Long userId = jwtProvider.getUserId(token);
                    String username = jwtProvider.getUsername(token);
                    var roles = jwtProvider.getRoles(token);

                    // Tạo principal (KHÔNG map authority ở đây nữa)
                    CustomUserPrincipal principal =
                            CustomUserPrincipal.fromJwt(userId, username, roles);

                    // Lấy authorities từ principal (chuẩn clean)
                    var authentication =
                            new UsernamePasswordAuthenticationToken(
                                    principal,
                                    token,
                                    principal.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace(); // có thể thay bằng logger
        }

        filterChain.doFilter(request, response);
    }
}