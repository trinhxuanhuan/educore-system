package com.educore.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    public List<String> getRoles(String token) {
        List<?> raw = extractAllClaims(token).get("roles", List.class);

        if (raw == null || raw.isEmpty()) return Collections.emptyList();

        return raw.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                .toList();
    }

    public Long getUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }
}