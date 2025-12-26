package com.example.demo.controller;

import com.example.demo.entity.Token;
import com.example.demo.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/tokens")
public class TokenController {
    
    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/counter/{counterId}")
    public ResponseEntity<Token> issueToken(@PathVariable Long counterId) {
        Token token = tokenService.issueToken(counterId);
        return ResponseEntity.ok(token);
    }

    @PutMapping("/{tokenId}/status")
    public ResponseEntity<Token> updateStatus(@PathVariable Long tokenId, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        Token token = tokenService.updateStatus(tokenId, status);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/{tokenId}")
    public ResponseEntity<Token> getToken(@PathVariable Long tokenId) {
        Token token = tokenService.getToken(tokenId);
        return ResponseEntity.ok(token);
    }
}