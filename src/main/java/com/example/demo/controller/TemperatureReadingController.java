package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/readings")
public class TemperatureReadingController {

    @PostMapping
    public ResponseEntity<String> createReading(@RequestBody Object readingRequest) {
        return ResponseEntity.ok("Temperature reading created successfully");
    }

    @GetMapping("/cold-room/{coldRoomId}")
    public ResponseEntity<String> getReadingsByColdRoom(@PathVariable Long coldRoomId) {
        return ResponseEntity.ok("Readings for cold room: " + coldRoomId);
    }
}