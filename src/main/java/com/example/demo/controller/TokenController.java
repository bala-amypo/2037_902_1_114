package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    @PostMapping("/issue/{counterId}")
    public ResponseEntity<String> issueToken(@PathVariable Long counterId) {
        return ResponseEntity.ok("Token issued for counter: " + counterId);
    }

    @PutMapping("/{tokenId}/status")
    public ResponseEntity<String> updateStatus(@PathVariable Long tokenId, @RequestParam String status) {
        return ResponseEntity.ok("Token " + tokenId + " status updated to: " + status);
    }

    @GetMapping("/{tokenId}")
    public ResponseEntity<String> getToken(@PathVariable Long tokenId) {
        return ResponseEntity.ok("Token details for ID: " + tokenId);
    }
}