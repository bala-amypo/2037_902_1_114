package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.TokenService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TokenServiceImpl implements TokenService {
    
    private static final String STATUS_WAITING = "WAITING";
    private static final String STATUS_SERVING = "SERVING";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_CANCELLED = "CANCELLED";
    
    private final TokenRepository tokenRepository;
    private final ServiceCounterRepository counterRepository;
    private final TokenLogRepository logRepository;
    private final QueuePositionRepository queueRepository;

    public TokenServiceImpl(TokenRepository tokenRepository, 
                           ServiceCounterRepository counterRepository,
                           TokenLogRepository logRepository,
                           QueuePositionRepository queueRepository) {
        this.tokenRepository = tokenRepository;
        this.counterRepository = counterRepository;
        this.logRepository = logRepository;
        this.queueRepository = queueRepository;
    }

    @Override
    public Token issueToken(Long counterId) {
        ServiceCounter counter = counterRepository.findById(counterId)
            .orElseThrow(() -> new RuntimeException("Counter not found"));
        
        if (!Boolean.TRUE.equals(counter.getIsActive())) {
            throw new IllegalArgumentException("Counter is not active");
        }

        Token token = new Token();
        token.setTokenNumber(generateTokenNumber(counter));
        token.setServiceCounter(counter);
        token.setStatus(STATUS_WAITING);
        token.setIssuedAt(LocalDateTime.now());
        
        token = tokenRepository.save(token);
        
        // Create queue position
        List<Token> waitingTokens = tokenRepository.findByServiceCounter_IdAndStatusOrderByIssuedAtAsc(counterId, STATUS_WAITING);
        QueuePosition queuePosition = new QueuePosition();
        queuePosition.setToken(token);
        queuePosition.setPosition(waitingTokens.size());
        queuePosition.setUpdatedAt(LocalDateTime.now());
        queueRepository.save(queuePosition);
        
        // Create log entry
        TokenLog log = new TokenLog();
        log.setToken(token);
        log.setLogMessage("Token issued");
        log.setLoggedAt(LocalDateTime.now());
        logRepository.save(log);
        
        return token;
    }

    @Override
    public Token updateStatus(Long tokenId, String status) {
        Token token = tokenRepository.findById(tokenId)
            .orElseThrow(() -> new RuntimeException("Token not found"));
        
        // Validate status transitions
        String currentStatus = token.getStatus();
        if (!isValidTransition(currentStatus, status)) {
            throw new IllegalArgumentException("Invalid status transition from " + currentStatus + " to " + status);
        }
        
        token.setStatus(status);
        if (STATUS_COMPLETED.equals(status) || STATUS_CANCELLED.equals(status)) {
            token.setCompletedAt(LocalDateTime.now());
        }
        
        token = tokenRepository.save(token);
        
        // Create log entry
        TokenLog log = new TokenLog();
        log.setToken(token);
        log.setLogMessage("Status updated to " + status);
        log.setLoggedAt(LocalDateTime.now());
        logRepository.save(log);
        
        return token;
    }

    @Override
    public Token getToken(Long tokenId) {
        return tokenRepository.findById(tokenId)
            .orElseThrow(() -> new RuntimeException("Token not found"));
    }

    private String generateTokenNumber(ServiceCounter counter) {
        return counter.getCounterName() + "-" + System.currentTimeMillis();
    }

    private boolean isValidTransition(String from, String to) {
        if (STATUS_WAITING.equals(from)) {
            return STATUS_SERVING.equals(to) || STATUS_CANCELLED.equals(to);
        }
        if (STATUS_SERVING.equals(from)) {
            return STATUS_COMPLETED.equals(to) || STATUS_CANCELLED.equals(to);
        }
        return false;
    }
}