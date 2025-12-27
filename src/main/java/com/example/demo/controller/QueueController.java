package com.example.demo.controller;

import com.example.demo.entity.QueuePosition;
import com.example.demo.service.QueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queue")
public class QueueController {
    
    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PutMapping("/position/{tokenId}/{newPosition}")
    public ResponseEntity<QueuePosition> updatePosition(@PathVariable Long tokenId, @PathVariable Integer newPosition) {
        QueuePosition position = queueService.updateQueuePosition(tokenId, newPosition);
        return ResponseEntity.ok(position);
    }

    @GetMapping("/position/{tokenId}")
    public ResponseEntity<QueuePosition> getPosition(@PathVariable Long tokenId) {
        QueuePosition position = queueService.getPosition(tokenId);
        return ResponseEntity.ok(position);
    }
}