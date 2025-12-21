

TokenLogController:
package com.example.demo.controller;

import com.example.demo.entity.TokenLog;
import com.example.demo.service.TokenLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/logs")
@Tag(name = "Logs", description = "Log management")
public class TokenLogController {
    
    private final TokenLogService tokenLogService;
    
    public TokenLogController(TokenLogService tokenLogService) {
        this.tokenLogService = tokenLogService;
    }
    
    @PostMapping("/{tokenId}")
    @Operation(summary = "Add log entry")
    public ResponseEntity<TokenLog> addLog(@PathVariable Long tokenId, @RequestParam String message) {
        TokenLog log = tokenLogService.addLog(tokenId, message);
        return ResponseEntity.ok(log);
    }
    
    @GetMapping("/{tokenId}")
    @Operation(summary = "Get logs for token")
    public ResponseEntity<List<TokenLog>> getLogs(@PathVariable Long tokenId) {
        List<TokenLog> logs = tokenLogService.getLogs(tokenId);
        return ResponseEntity.ok(logs);
    }
}
