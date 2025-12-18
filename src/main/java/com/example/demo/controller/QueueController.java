package com.example.demo.controller;

import com.example.demo.entity.QueuePosition;
import com.example.demo.service.QueueService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queue")
@Tag(name = "Queue Management")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PutMapping("/position/{tokenId}/{newPosition}")
    @Operation(summary = "Update queue position")
    public QueuePosition updateQueuePosition(
            @PathVariable Long tokenId,
            @PathVariable Integer newPosition) {

        return queueService.updateQueuePosition(tokenId, newPosition);
    }

    @GetMapping("/position/{tokenId}")
    @Operation(summary = "Get queue position for token")
    public QueuePosition getPosition(@PathVariable Long tokenId) {
        return queueService.getPosition(tokenId);
    }
}