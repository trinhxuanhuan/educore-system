package com.stuman.auth_service.security;

import com.stuman.auth_service.entity.User;
import com.stuman.auth_service.exception.BaseException;
import com.stuman.auth_service.exception.ErrorCode;
import com.stuman.auth_service.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    private final UserRepository userRepository;

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expiration}")
    private long jwtExpirationMs;

    public JwtProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ================= CORE =================

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // ================= GENERATE TOKEN =================

    public String generateToken(Authentication authentication) {

        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        //LẤY USER TỪ DB → LẤY ID
        User user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // Roles
        List<String> roles = principal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim(JwtConstants.ROLES, roles)
                .claim(JwtConstants.USER_ID, user.getId())   // ⭐ QUAN TRỌNG NHẤT
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + jwtExpirationMs)
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ================= PARSE =================

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        return getClaims(token).get(JwtConstants.ROLES, List.class);
    }

    public Long getUserIdFromToken(String token) {
        return getClaims(token).get(JwtConstants.USER_ID, Long.class);
    }

    // ================= VALIDATE =================

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
