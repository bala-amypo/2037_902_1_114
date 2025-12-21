package com.example.demo.controller;

import com.example.demo.entity.ServiceCounter;
import com.example.demo.service.ServiceCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/counters")
@Tag(name = "Counters", description = "Service counter management")
public class ServiceCounterController {
    
    private final ServiceCounterService serviceCounterService;
    
    public ServiceCounterController(ServiceCounterService serviceCounterService) {
        this.serviceCounterService = serviceCounterService;
    }
    
    @PostMapping
    @Operation(summary = "Add service counter")
    public ResponseEntity<ServiceCounter> addCounter(@RequestBody ServiceCounter counter) {
        ServiceCounter saved = serviceCounterService.addCounter(counter);
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active counters")
    public ResponseEntity<List<ServiceCounter>> getActiveCounters() {
        List<ServiceCounter> counters = serviceCounterService.getActiveCounters();
        return ResponseEntity.ok(counters);
    }
}

