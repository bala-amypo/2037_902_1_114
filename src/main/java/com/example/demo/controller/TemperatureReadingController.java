emperatureReadingController:
package com.example.demo.controller;

import com.example.demo.dto.TemperatureReadingRequest;
import com.example.demo.entity.TemperatureReading;
import com.example.demo.service.TemperatureReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/readings")
@Tag(name = "Readings", description = "Temperature reading management")
public class TemperatureReadingController {
    
    private final TemperatureReadingService temperatureReadingService;
    
    public TemperatureReadingController(TemperatureReadingService temperatureReadingService) {
        this.temperatureReadingService = temperatureReadingService;
    }
    
    @PostMapping
    @Operation(summary = "Record temperature reading")
    public ResponseEntity<TemperatureReading> recordReading(@RequestBody TemperatureReadingRequest request) {
        TemperatureReading reading = temperatureReadingService.recordReading(
                request.getSensorIdentifier(), request.getReadingValue());
        return ResponseEntity.ok(reading);
    }
}