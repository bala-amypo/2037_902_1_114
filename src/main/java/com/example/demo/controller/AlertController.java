package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    @GetMapping
    public ResponseEntity<String> getAllAlerts() {
        return ResponseEntity.ok("List of alerts");
    }

    @PostMapping
    public ResponseEntity<String> createAlert(@RequestBody Object alertRequest) {
        return ResponseEntity.ok("Alert created successfully");
    }
}