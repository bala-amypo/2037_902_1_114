package com.example.demo.entity;

public class SensorDevice {
    private Long id;
    private String identifier;
    private ColdRoom coldRoom;
    private Boolean isActive = true;

    public SensorDevice() {}

    public SensorDevice(String identifier, ColdRoom coldRoom, Boolean isActive) {
        this.identifier = identifier;
        this.coldRoom = coldRoom;
        this.isActive = isActive;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public ColdRoom getColdRoom() { return coldRoom; }
    public void setColdRoom(ColdRoom coldRoom) { this.coldRoom = coldRoom; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}