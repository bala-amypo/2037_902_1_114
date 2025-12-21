package com.example.demo.service.impl;

import com.example.demo.entity.BreachAlert;
import com.example.demo.entity.QueuePosition;
import com.example.demo.entity.ServiceCounter;
import com.example.demo.entity.TokenLog;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.QueuePositionRepository;
import com.example.demo.repository.ServiceCounterRepository;
import com.example.demo.repository.TokenLogRepository;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.TokenService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {
    
    private final TokenRepository tokenRepository;
    private final ServiceCounterRepository serviceCounterRepository;
    private final TokenLogRepository tokenLogRepository;
    private final QueuePositionRepository queuePositionRepository;
    
    public TokenServiceImpl(TokenRepository tokenRepository, ServiceCounterRepository serviceCounterRepository, 
                           TokenLogRepository tokenLogRepository, QueuePositionRepository queuePositionRepository) {
        this.tokenRepository = tokenRepository;
        this.serviceCounterRepository = serviceCounterRepository;
        this.tokenLogRepository = tokenLogRepository;
        this.queuePositionRepository = queuePositionRepository;
    }
    
    @Override
    public BreachAlert issueToken(Long counterId) {
        ServiceCounter counter = serviceCounterRepository.findById(counterId)
                .orElseThrow(() -> new ResourceNotFoundException("Counter not found"));
        
        if (!counter.getIsActive()) {
            throw new IllegalArgumentException("Counter is not active");
        }
        
        BreachAlert token = new BreachAlert();
        token.setTokenNumber(UUID.randomUUID().toString());
        token.setStatus("OPEN");
        token.setIssuedAt(LocalDateTime.now());
        token.setServiceCounter(counter);
        
        token = tokenRepository.save(token);
        
        QueuePosition queuePosition = new QueuePosition(token, 1, LocalDateTime.now());
        queuePositionRepository.save(queuePosition);
        
        TokenLog log = new TokenLog(token, "Token issued", LocalDateTime.now());
        tokenLogRepository.save(log);
        
        return token;
    }
    
    @Override
    public BreachAlert updateStatus(Long tokenId, String status) {
        BreachAlert token = getToken(tokenId);
        
        String currentStatus = token.getStatus();
        if (!isValidTransition(currentStatus, status)) {
            throw new IllegalArgumentException("Invalid status transition from " + currentStatus + " to " + status);
        }
        
        token.setStatus(status);
        if ("RESOLVED".equals(status) || "CANCELLED".equals(status)) {
            token.setResolvedAt(LocalDateTime.now());
        }
        
        token = tokenRepository.save(token);
        
        TokenLog log = new TokenLog(token, "Status updated to " + status, LocalDateTime.now());
        tokenLogRepository.save(log);
        
        return token;
    }
    
    @Override
    public BreachAlert getToken(Long tokenId) {
        return tokenRepository.findById(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));
    }
    
    private boolean isValidTransition(String from, String to) {
        if ("OPEN".equals(from)) {
            return "ACKNOWLEDGED".equals(to) || "CANCELLED".equals(to);
        }
        if ("ACKNOWLEDGED".equals(from)) {
            return "RESOLVED".equals(to) || "CANCELLED".equals(to);
        }
        return false;
    }
}
