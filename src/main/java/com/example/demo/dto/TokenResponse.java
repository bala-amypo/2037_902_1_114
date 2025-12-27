package com.example.demo.dto;

import java.time.LocalDateTime;

public class TokenResponse {
    private Long id;
    private String tokenNumber;
    private String status;
    private LocalDateTime issuedAt;
    private LocalDateTime completedAt;
    private String counterName;
    private Integer queuePosition;

    public TokenResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(String tokenNumber) { this.tokenNumber = tokenNumber; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public String getCounterName() { return counterName; }
    public void setCounterName(String counterName) { this.counterName = counterName; }
    
    public Integer getQueuePosition() { return queuePosition; }
    public void setQueuePosition(Integer queuePosition) { this.queuePosition = queuePosition; }
}
