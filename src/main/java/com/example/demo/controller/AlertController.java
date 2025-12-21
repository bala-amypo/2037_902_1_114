package com.example.demo.controller;

import com.example.demo.entity.BreachAlert;
import com.example.demo.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alerts")
@Tag(name = "Alerts", description = "Alert management")
public class AlertController {
    
    private final TokenService tokenService;
    
    public AlertController(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    
    @PostMapping("/counter/{counterId}")
    @Operation(summary = "Issue new alert")
    public ResponseEntity<BreachAlert> issueAlert(@PathVariable Long counterId) {
        BreachAlert alert = tokenService.issueToken(counterId);
        return ResponseEntity.ok(alert);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update alert status")
    public ResponseEntity<BreachAlert> updateStatus(@PathVariable Long id, @RequestParam String status) {
        BreachAlert alert = tokenService.updateStatus(id, status);
        return ResponseEntity.ok(alert);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get alert details")
    public ResponseEntity<BreachAlert> getAlert(@PathVariable Long id) {
        BreachAlert alert = tokenService.getToken(id);
        return ResponseEntity.ok(alert);
    }
}

