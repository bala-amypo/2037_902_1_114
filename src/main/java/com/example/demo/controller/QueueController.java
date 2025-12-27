package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queue")
public class QueueController {

    @PutMapping("/position/{tokenId}/{newPosition}")
    public ResponseEntity<String> updateQueuePosition(@PathVariable Long tokenId, @PathVariable Integer newPosition) {
        return ResponseEntity.ok("Queue position updated for token: " + tokenId + " to position: " + newPosition);
    }

    @GetMapping("/position/{tokenId}")
    public ResponseEntity<String> getQueuePosition(@PathVariable Long tokenId) {
        return ResponseEntity.ok("Queue position for token: " + tokenId);
    }
}
