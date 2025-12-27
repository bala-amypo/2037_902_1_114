package com.example.demo.config;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    public String generateToken(Long id, String email, String role) {
    return Jwts.builder()
            .setSubject(email)
            .claim("id", id)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
}


    public String getUsernameFromToken(String token) {
        return token.replace("dummy-token-", "");
    }

    public boolean validateToken(String token) {
        return token != null && token.startsWith("dummy-token-");
    }
}