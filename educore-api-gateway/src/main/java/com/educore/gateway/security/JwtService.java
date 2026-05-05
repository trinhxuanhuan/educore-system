package com.educore.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

@Slf4j
@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    private Key getSignKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public String getUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String getRole(String token) {
        var roles = extractAllClaims(token).get("roles", java.util.List.class);

        if (roles == null || roles.isEmpty()) return null;

        String role = roles.get(0).toString();

        // bỏ ROLE_
        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }

        return role;
    }

    public Long getUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }
}