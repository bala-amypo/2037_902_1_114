package com.example.demo.controller;

import com.example.demo.entity.ServiceCounter;
import com.example.demo.service.ServiceCounterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/counters")
@Tag(name = "Service Counters")
public class ServiceCounterController {

    private final ServiceCounterService serviceCounterService;

    public ServiceCounterController(ServiceCounterService serviceCounterService) {
        this.serviceCounterService = serviceCounterService;
    }

    @PostMapping
    @Operation(summary = "Add new service counter")
    public ServiceCounter addCounter(@RequestBody ServiceCounter counter) {
        return serviceCounterService.addCounter(counter);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active service counters")
    public List<ServiceCounter> getActiveCounters() {
        return serviceCounterService.getActiveCounters();
    }
}