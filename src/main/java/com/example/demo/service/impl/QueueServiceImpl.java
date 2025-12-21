package com.example.demo.service.impl;

import com.example.demo.entity.QueuePosition;
import com.example.demo.repository.QueuePositionRepository;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.QueueService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class QueueServiceImpl implements QueueService {
    
    private final QueuePositionRepository queuePositionRepository;
    private final TokenRepository tokenRepository;
    
    public QueueServiceImpl(QueuePositionRepository queuePositionRepository, TokenRepository tokenRepository) {
        this.queuePositionRepository = queuePositionRepository;
        this.tokenRepository = tokenRepository;
    }
    
    @Override
    public QueuePosition updateQueuePosition(Long tokenId, Integer newPosition) {
        if (newPosition < 1) {
            throw new IllegalArgumentException("Position must be at least 1");
        }
        
        QueuePosition queuePosition = queuePositionRepository.findByTokenId(tokenId)
                .orElse(new QueuePosition());
        
        if (queuePosition.getToken() == null) {
            queuePosition.setToken(tokenRepository.findById(tokenId)
                    .orElseThrow(() -> new RuntimeException("Token not found")));
        }
        
        queuePosition.setPosition(newPosition);
        queuePosition.setUpdatedAt(LocalDateTime.now());
        
        return queuePositionRepository.save(queuePosition);
    }
    
    @Override
    public QueuePosition getPosition(Long tokenId) {
        return queuePositionRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new RuntimeException("Queue position not found"));
    }
}
