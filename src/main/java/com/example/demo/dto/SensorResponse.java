package com.example.demo.dto;

public class SensorResponse {
    private Long id;
    private String identifier;
    private Long coldRoomId;
    private String coldRoomName;
    private Boolean isActive;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public Long getColdRoomId() { return coldRoomId; }
    public void setColdRoomId(Long coldRoomId) { this.coldRoomId = coldRoomId; }
    public String getColdRoomName() { return coldRoomName; }
    public void setColdRoomName(String coldRoomName) { this.coldRoomName = coldRoomName; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}