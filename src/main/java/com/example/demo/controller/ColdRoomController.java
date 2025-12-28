package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cold-rooms")
public class ColdRoomController {

    @GetMapping
    public ResponseEntity<String> getAllColdRooms() {
        return ResponseEntity.ok("List of cold rooms");
    }

    @PostMapping
    public ResponseEntity<String> createColdRoom(@RequestBody Object coldRoomRequest) {
        return ResponseEntity.ok("Cold room created successfully");
    }
}