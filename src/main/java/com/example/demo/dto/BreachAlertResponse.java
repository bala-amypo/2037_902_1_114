package com.example.demo.dto;

import java.time.LocalDateTime;

public class BreachAlertResponse {
    private Long id;
    private String tokenNumber;
    private String coldRoomName;
    private String sensorIdentifier;
    private String breachType;
    private String status;
    private LocalDateTime issuedAt;
    private LocalDateTime resolvedAt;
    private Integer queuePosition;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(String tokenNumber) { this.tokenNumber = tokenNumber; }
    public String getColdRoomName() { return coldRoomName; }
    public void setColdRoomName(String coldRoomName) { this.coldRoomName = coldRoomName; }
    public String getSensorIdentifier() { return sensorIdentifier; }
    public void setSensorIdentifier(String sensorIdentifier) { this.sensorIdentifier = sensorIdentifier; }
    public String getBreachType() { return breachType; }
    public void setBreachType(String breachType) { this.breachType = breachType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public Integer getQueuePosition() { return queuePosition; }
    public void setQueuePosition(Integer queuePosition) { this.queuePosition = queuePosition; }
}