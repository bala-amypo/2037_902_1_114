package com.example.demo.controller;

import com.example.demo.entity.TokenLog;
import com.example.demo.service.TokenLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/logs")
public class TokenLogController {
    
    private final TokenLogService logService;

    public TokenLogController(TokenLogService logService) {
        this.logService = logService;
    }

    @PostMapping("/{tokenId}")
    public ResponseEntity<TokenLog> addLog(@PathVariable Long tokenId, @RequestBody Map<String, String> request) {
        String message = request.get("message");
        TokenLog log = logService.addLog(tokenId, message);
        return ResponseEntity.ok(log);
    }

    @GetMapping("/{tokenId}")
    public ResponseEntity<List<TokenLog>> getLogs(@PathVariable Long tokenId) {
        List<TokenLog> logs = logService.getLogs(tokenId);
        return ResponseEntity.ok(logs);
    }
}