package com.example.demo.dto;

public class ColdRoomResponse {
    private Long id;
    private String name;
    private String location;
    private Double minAllowed;
    private Double maxAllowed;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Double getMinAllowed() { return minAllowed; }
    public void setMinAllowed(Double minAllowed) { this.minAllowed = minAllowed; }
    public Double getMaxAllowed() { return maxAllowed; }
    public void setMaxAllowed(Double maxAllowed) { this.maxAllowed = maxAllowed; }
}