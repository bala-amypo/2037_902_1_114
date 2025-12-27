package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_logs")
public class TokenLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "token_id")
    private Token token;
    
    private String logMessage;
    private LocalDateTime loggedAt = LocalDateTime.now();

    public TokenLog() {}

    public TokenLog(Token token, String logMessage, LocalDateTime loggedAt) {
        this.token = token;
        this.logMessage = logMessage;
        this.loggedAt = loggedAt;
    }

    @PrePersist
    public void prePersist() {
        if (loggedAt == null) {
            loggedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Token getToken() { return token; }
    public void setToken(Token token) { this.token = token; }
    
    public String getLogMessage() { return logMessage; }
    public void setLogMessage(String logMessage) { this.logMessage = logMessage; }
    
    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }
}