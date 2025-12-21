package com.example.demo.service;

import com.example.demo.entity.TemperatureReading;

public interface TemperatureReadingService {
    TemperatureReading recordReading(String sensorIdentifier, Double readingValue);
}