package com.educore.student.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.List;

@Component
public class JwtProvider {

    @Value("${security.jwt.secret}")
    private String secretKey;

    // ================= CORE =================
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ================= EXTRACT INFO =================

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Object roles = getClaims(token).get("roles");

        if (roles instanceof List<?>) {
            return ((List<?>) roles).stream()
                    .map(Object::toString) // FIX QUAN TRỌNG
                    .toList();
        }

        return Collections.emptyList();
    }

    public Long getUserId(String token) {
        Number userId = getClaims(token).get("userId", Number.class);
        return userId != null ? userId.longValue() : null;
    }

    // ================= VALIDATE =================

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("JWT INVALID: " + e.getMessage()); // debug nhẹ
            return false;
        }
    }
}