package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
public class TokenLogController {

    @PostMapping("/{tokenId}")
    public ResponseEntity<String> addLog(@PathVariable Long tokenId, @RequestBody String message) {
        return ResponseEntity.ok("Log added for token: " + tokenId);
    }

    @GetMapping("/{tokenId}")
    public ResponseEntity<String> getLogs(@PathVariable Long tokenId) {
        return ResponseEntity.ok("Logs for token: " + tokenId);
    }
}