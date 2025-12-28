package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sensors")
public class SensorController {

    @GetMapping
    public ResponseEntity<String> getAllSensors() {
        return ResponseEntity.ok("List of sensors");
    }

    @PostMapping
    public ResponseEntity<String> createSensor(@RequestBody Object sensorRequest) {
        return ResponseEntity.ok("Sensor created successfully");
    }
}