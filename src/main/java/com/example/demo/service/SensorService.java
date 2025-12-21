package com.example.demo.service;

import com.example.demo.entity.SensorDevice;
import java.util.List;

public interface SensorService {
    SensorDevice createSensor(SensorDevice sensor);
    SensorDevice findByIdentifier(String identifier);
    List<SensorDevice> getAllSensors();
}
