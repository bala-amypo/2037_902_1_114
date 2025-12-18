package com.example.demo.service;

import com.example.demo.entity.QueuePosition;
import com.example.demo.entity.Token;
import com.example.demo.repository.QueuePositionRepository;
import com.example.demo.repository.TokenRepository;

import org.springframework.stereotype.Service;

@Service
public class QueueService {

    private final QueuePositionRepository queuePositionRepository;
    private final TokenRepository tokenRepository;

    public QueueService(QueuePositionRepository queuePositionRepository,
                        TokenRepository tokenRepository) {
        this.queuePositionRepository = queuePositionRepository;
        this.tokenRepository = tokenRepository;
    }

    public QueuePosition updateQueuePosition(Long tokenId, Integer newPosition) {

        if (newPosition < 1) {
            throw new RuntimeException("Invalid position");
        }

        QueuePosition queuePosition = queuePositionRepository.findByToken_Id(tokenId)
                .orElseThrow(() -> new RuntimeException("Queue position not found"));

        queuePosition.setPosition(newPosition);
        return queuePositionRepository.save(queuePosition);
    }

    public QueuePosition getPosition(Long tokenId) {
        return queuePositionRepository.findByToken_Id(tokenId)
                .orElseThrow(() -> new RuntimeException("Queue position not found"));
    }
}