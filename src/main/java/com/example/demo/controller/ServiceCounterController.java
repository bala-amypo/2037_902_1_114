ServiceCounterController:
package com.example.demo.controller;

import com.example.demo.entity.ServiceCounter;
import com.example.demo.service.ServiceCounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/counters")
public class ServiceCounterController {
    
    private final ServiceCounterService counterService;

    public ServiceCounterController(ServiceCounterService counterService) {
        this.counterService = counterService;
    }

    @PostMapping
    public ResponseEntity<ServiceCounter> addCounter(@RequestBody ServiceCounter counter) {
        ServiceCounter created = counterService.addCounter(counter);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ServiceCounter>> getActiveCounters() {
        List<ServiceCounter> counters = counterService.getActiveCounters();
        return ResponseEntity.ok(counters);
    }
}
