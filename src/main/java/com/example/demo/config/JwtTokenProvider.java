package com.example.demo.config;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    public String generateToken(String username) {
        return "dummy-token-" + username;
    }

    public String getUsernameFromToken(String token) {
        return token.replace("dummy-token-", "");
    }

    public boolean validateToken(String token) {
        return token != null && token.startsWith("dummy-token-");
    }
}