package com.example.demo.controller;

import com.example.demo.entity.TokenLog;
import com.example.demo.service.TokenLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
@Tag(name = "Token Logs")
public class TokenLogController {

    private final TokenLogService tokenLogService;

    public TokenLogController(TokenLogService tokenLogService) {
        this.tokenLogService = tokenLogService;
    }

    @PostMapping("/{tokenId}")
    @Operation(summary = "Add token log")
    public TokenLog addLog(
            @PathVariable Long tokenId,
            @RequestParam String message) {

        return tokenLogService.addLog(tokenId, message);
    }

    @GetMapping("/{tokenId}")
    @Operation(summary = "Get logs for token")
    public List<TokenLog> getLogs(@PathVariable Long tokenId) {
        return tokenLogService.getLogs(tokenId);
    }
}