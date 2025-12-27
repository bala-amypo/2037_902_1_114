package com.example.demo.service.impl;

import com.example.demo.entity.ServiceCounter;
import com.example.demo.entity.Token;
import com.example.demo.entity.TokenLog;
import com.example.demo.entity.QueuePosition;
import com.example.demo.repository.TokenRepository;
import com.example.demo.repository.ServiceCounterRepository;
import com.example.demo.repository.TokenLogRepository;
import com.example.demo.repository.QueuePositionRepository;
import com.example.demo.service.TokenService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final ServiceCounterRepository counterRepository;
    private final TokenLogRepository logRepository;
    private final QueuePositionRepository queueRepository;

    public TokenServiceImpl(TokenRepository tokenRepository, ServiceCounterRepository counterRepository, 
                           TokenLogRepository logRepository, QueuePositionRepository queueRepository) {
        this.tokenRepository = tokenRepository;
        this.counterRepository = counterRepository;
        this.logRepository = logRepository;
        this.queueRepository = queueRepository;
    }

    public Token issueToken(Long counterId) {
        ServiceCounter counter = counterRepository.findById(counterId)
                .orElseThrow(() -> new RuntimeException("Counter not found"));
        
        if (!counter.getIsActive()) {
            throw new IllegalArgumentException("Counter is not active");
        }

        Token token = new Token();
        token.setTokenNumber("T-" + System.currentTimeMillis());
        token.setServiceCounter(counter);
        token.setStatus("WAITING");
        token.setIssuedAt(LocalDateTime.now());
        
        Token savedToken = tokenRepository.save(token);
        
        // Create queue position
        List<Token> waitingTokens = tokenRepository.findByServiceCounter_IdAndStatusOrderByIssuedAtAsc(counterId, "WAITING");
        QueuePosition queuePosition = new QueuePosition();
        queuePosition.setToken(savedToken);
        queuePosition.setPosition(waitingTokens.size());
        queuePosition.setUpdatedAt(LocalDateTime.now());
        queueRepository.save(queuePosition);
        
        // Create log
        TokenLog log = new TokenLog();
        log.setToken(savedToken);
        log.setLogMessage("Token issued");
        logRepository.save(log);
        
        return savedToken;
    }

    public Token updateStatus(Long tokenId, String newStatus) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        
        String currentStatus = token.getStatus();
        
        // Validate status transitions
        if ("WAITING".equals(currentStatus) && "COMPLETED".equals(newStatus)) {
            throw new IllegalArgumentException("Invalid status transition");
        }
        
        token.setStatus(newStatus);
        
        if ("COMPLETED".equals(newStatus) || "CANCELLED".equals(newStatus)) {
            token.setCompletedAt(LocalDateTime.now());
        }
        
        Token savedToken = tokenRepository.save(token);
        
        // Create log
        TokenLog log = new TokenLog();
        log.setToken(savedToken);
        log.setLogMessage("Status updated to " + newStatus);
        logRepository.save(log);
        
        return savedToken;
    }

    public Token getToken(Long tokenId) {
        return tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));
    }
}
