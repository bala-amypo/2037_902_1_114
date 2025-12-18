package com.example.demo.controller;

import com.example.demo.entity.Token;
import com.example.demo.service.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tokens")
@Tag(name = "Tokens")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/issue/{counterId}")
    @Operation(summary = "Issue new token")
    public Token issueToken(@PathVariable Long counterId) {
        return tokenService.issueToken(counterId);
    }

    @PutMapping("/status/{tokenId}")
    @Operation(summary = "Update token status")
    public Token updateStatus(
            @PathVariable Long tokenId,
            @RequestParam String status) {

        return tokenService.updateStatus(tokenId, status);
    }

    @GetMapping("/{tokenId}")
    @Operation(summary = "Get token details")
    public Token getToken(@PathVariable Long tokenId) {
        return tokenService.getToken(tokenId);
    }
}