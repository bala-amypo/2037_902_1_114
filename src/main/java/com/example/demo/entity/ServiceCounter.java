package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "service_counters")
public class ServiceCounter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String counterName;
    private String department;
    private Boolean isActive = true;

    public ServiceCounter() {}

    public ServiceCounter(String counterName, String department, Boolean isActive) {
        this.counterName = counterName;
        this.department = department;
        this.isActive = isActive;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCounterName() { return counterName; }
    public void setCounterName(String counterName) { this.counterName = counterName; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}