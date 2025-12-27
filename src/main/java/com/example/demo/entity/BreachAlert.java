package com.example.demo.entity;

import java.time.LocalDateTime;

public class BreachAlert {
    private Long id;
    private String tokenNumber;
    private ColdRoom coldRoom;
    private SensorDevice sensor;
    private TemperatureReading reading;
    private String status = "OPEN";
    private String breachType;
    private LocalDateTime issuedAt;
    private LocalDateTime resolvedAt;

    public BreachAlert() {}

    public BreachAlert(String tokenNumber, ColdRoom coldRoom, SensorDevice sensor, 
                      TemperatureReading reading, String status, String breachType, 
                      LocalDateTime issuedAt, LocalDateTime resolvedAt) {
        this.tokenNumber = tokenNumber;
        this.coldRoom = coldRoom;
        this.sensor = sensor;
        this.reading = reading;
        this.status = status;
        this.breachType = breachType;
        this.issuedAt = issuedAt;
        this.resolvedAt = resolvedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(String tokenNumber) { this.tokenNumber = tokenNumber; }
    public ColdRoom getColdRoom() { return coldRoom; }
    public void setColdRoom(ColdRoom coldRoom) { this.coldRoom = coldRoom; }
    public SensorDevice getSensor() { return sensor; }
    public void setSensor(SensorDevice sensor) { this.sensor = sensor; }
    public TemperatureReading getReading() { return reading; }
    public void setReading(TemperatureReading reading) { this.reading = reading; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBreachType() { return breachType; }
    public void setBreachType(String breachType) { this.breachType = breachType; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}