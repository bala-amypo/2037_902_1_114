package com.example.demo.service.impl;

import com.example.demo.entity.SensorDevice;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.SensorDeviceRepository;
import com.example.demo.service.SensorService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SensorServiceImpl implements SensorService {
    
    private final SensorDeviceRepository sensorDeviceRepository;
    
    public SensorServiceImpl(SensorDeviceRepository sensorDeviceRepository) {
        this.sensorDeviceRepository = sensorDeviceRepository;
    }
    
    @Override
    public SensorDevice createSensor(SensorDevice sensor) {
        if (sensorDeviceRepository.findByIdentifier(sensor.getIdentifier()).isPresent()) {
            throw new IllegalArgumentException("Sensor identifier already exists");
        }
        return sensorDeviceRepository.save(sensor);
    }
    
    @Override
    public SensorDevice findByIdentifier(String identifier) {
        return sensorDeviceRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor not found"));
    }
    
    @Override
    public List<SensorDevice> getAllSensors() {
        return sensorDeviceRepository.findAll();
    }
}

