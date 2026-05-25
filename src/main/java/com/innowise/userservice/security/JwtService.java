package com.innowise.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public boolean isTokenValid(String token) {

        try {
            extractAllClaims(token);
            return true;

        } catch (JwtException e) {
            return false;
        }
    }

    public String extractLogin(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);

        return claims.get("role", String.class);
    }

    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);

        return claims.get("userId", Long.class);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
