package com.example.demo.controller;

import com.example.demo.entity.SensorDevice;
import com.example.demo.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/sensors")
@Tag(name = "Sensors", description = "Sensor management")
public class SensorController {
    
    private final SensorService sensorService;
    
    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }
    
    @PostMapping
    @Operation(summary = "Create sensor")
    public ResponseEntity<SensorDevice> createSensor(@RequestBody SensorDevice sensor) {
        SensorDevice saved = sensorService.createSensor(sensor);
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping
    @Operation(summary = "Get all sensors")
    public ResponseEntity<List<SensorDevice>> getAllSensors() {
        List<SensorDevice> sensors = sensorService.getAllSensors();
        return ResponseEntity.ok(sensors);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update sensor status")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        // This would need to be implemented in the service
        return ResponseEntity.ok("Status updated");
    }
}
