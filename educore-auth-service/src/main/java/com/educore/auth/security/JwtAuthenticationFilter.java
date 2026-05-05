package com.educore.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtProvider jwtProvider,
            UserDetailsService userDetailsService
    ) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Bỏ qua JWT filter cho các API public & internal, cũng như Swagger/OpenAPI endpoints
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        return (path.equals("/api/v1/auth/login") && method.equals("POST"))
                || (path.equals("/api/v1/auth/register") && method.equals("POST"))
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/swagger-ui.html");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String bearer = request.getHeader("Authorization");

            // Không có token → cho đi tiếp
            if (bearer == null || !bearer.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = bearer.substring(7);

            // Token không hợp lệ → cho đi tiếp (Security sẽ chặn sau)
            if (!jwtProvider.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtProvider.getUsernameFromToken(token);

            // Tránh set authentication nhiều lần
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 1. Lấy danh sách roles trực tiếp từ Token (Tiết kiệm 1 lần gọi DB)
                List<String> roles = jwtProvider.getRolesFromToken(token);

                // 2. Chuyển String thành GrantedAuthority (Giữ nguyên ROLE_ADMIN từ bước 1)
                List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities = roles.stream()
                        .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // 3. Tạo authentication mà không cần load lại UserDetails (Tối ưu cho Microservices)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            logger.error("Cannot set user authentication for path " + request.getServletPath(), ex);
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        }
    }
}
