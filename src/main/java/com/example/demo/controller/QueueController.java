package com.example.demo.controller;

import com.example.demo.entity.QueuePosition;
import com.example.demo.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queue")
@Tag(name = "Queue", description = "Queue management")
public class QueueController {
    
    private final QueueService queueService;
    
    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }
    
    @PutMapping("/position/{tokenId}/{newPosition}")
    @Operation(summary = "Update queue position")
    public ResponseEntity<QueuePosition> updatePosition(@PathVariable Long tokenId, @PathVariable Integer newPosition) {
        QueuePosition position = queueService.updateQueuePosition(tokenId, newPosition);
        return ResponseEntity.ok(position);
    }
    
    @GetMapping("/position/{tokenId}")
    @Operation(summary = "Get queue position")
    public ResponseEntity<QueuePosition> getPosition(@PathVariable Long tokenId) {
        QueuePosition position = queueService.getPosition(tokenId);
        return ResponseEntity.ok(position);
    }
}
