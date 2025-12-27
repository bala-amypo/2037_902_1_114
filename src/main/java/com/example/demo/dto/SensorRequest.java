package com.example.demo.dto;

public class SensorRequest {
    private String identifier;
    private Long coldRoomId;
    private Boolean isActive;

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public Long getColdRoomId() { return coldRoomId; }
    public void setColdRoomId(Long coldRoomId) { this.coldRoomId = coldRoomId; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}