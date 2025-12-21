package com.example.demo.service.impl;

import com.example.demo.entity.BreachAlert;
import com.example.demo.entity.SensorDevice;
import com.example.demo.entity.TemperatureReading;
import com.example.demo.repository.TemperatureReadingRepository;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.SensorService;
import com.example.demo.service.TemperatureReadingService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TemperatureReadingServiceImpl implements TemperatureReadingService {
    
    private final TemperatureReadingRepository temperatureReadingRepository;
    private final SensorService sensorService;
    private final TokenRepository tokenRepository;
    
    public TemperatureReadingServiceImpl(TemperatureReadingRepository temperatureReadingRepository, 
                                       SensorService sensorService, TokenRepository tokenRepository) {
        this.temperatureReadingRepository = temperatureReadingRepository;
        this.sensorService = sensorService;
        this.tokenRepository = tokenRepository;
    }
    
    @Override
    public TemperatureReading recordReading(String sensorIdentifier, Double readingValue) {
        SensorDevice sensor = sensorService.findByIdentifier(sensorIdentifier);
        
        if (!sensor.getIsActive()) {
            throw new IllegalArgumentException("Sensor is not active");
        }
        
        TemperatureReading reading = new TemperatureReading(sensor, sensor.getColdRoom(), readingValue, LocalDateTime.now());
        reading = temperatureReadingRepository.save(reading);
        
        // Check for breach
        var coldRoom = sensor.getColdRoom();
        if (readingValue < coldRoom.getMinAllowed() || readingValue > coldRoom.getMaxAllowed()) {
            BreachAlert alert = new BreachAlert();
            alert.setTokenNumber(UUID.randomUUID().toString());
            alert.setColdRoom(coldRoom);
            alert.setSensor(sensor);
            alert.setReading(reading);
            alert.setStatus("OPEN");
            alert.setBreachType(readingValue < coldRoom.getMinAllowed() ? "LOW" : "HIGH");
            alert.setIssuedAt(LocalDateTime.now());
            tokenRepository.save(alert);
        }
        
        return reading;
    }
}
