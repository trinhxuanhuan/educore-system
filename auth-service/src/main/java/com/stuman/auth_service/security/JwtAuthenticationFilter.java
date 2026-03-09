package com.stuman.auth_service.security;

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
     * Bỏ qua JWT filter cho các API public & internal
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        return (path.equals("/api/v1/auth/login") && method.equals("POST"))
                || (path.equals("/api/v1/auth/register") && method.equals("POST"));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String bearer = request.getHeader("Authorization");

        //Không có token → cho đi tiếp
        if (bearer == null || !bearer.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = bearer.substring(7);

        //Token không hợp lệ → cho đi tiếp (Security sẽ chặn sau)
        if (!jwtProvider.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtProvider.getUsernameFromToken(token);

        //Tránh set authentication nhiều lần
        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
