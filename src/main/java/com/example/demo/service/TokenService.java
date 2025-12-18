package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;
    private final ServiceCounterRepository serviceCounterRepository;
    private final TokenLogRepository tokenLogRepository;
    private final QueuePositionRepository queuePositionRepository;

    public TokenService(TokenRepository tokenRepository,
                        ServiceCounterRepository serviceCounterRepository,
                        TokenLogRepository tokenLogRepository,
                        QueuePositionRepository queuePositionRepository) {

        this.tokenRepository = tokenRepository;
        this.serviceCounterRepository = serviceCounterRepository;
        this.tokenLogRepository = tokenLogRepository;
        this.queuePositionRepository = queuePositionRepository;
    }

    public Token issueToken(Long counterId) {

        ServiceCounter counter = serviceCounterRepository.findById(counterId)
                .orElseThrow(() -> new RuntimeException("Counter not found"));

        if (!Boolean.TRUE.equals(counter.getIsActive())) {
            throw new RuntimeException("Counter not active");
        }

        Token token = new Token();
        token.setTokenNumber("TKN-" + System.currentTimeMillis());
        token.setServiceCounter(counter);
        token.setStatus("WAITING");
        token.setIssuedAt(LocalDateTime.now());

        Token savedToken = tokenRepository.save(token);

        QueuePosition position = new QueuePosition(savedToken, 1);
        queuePositionRepository.save(position);

        TokenLog log = new TokenLog(savedToken, "Token issued");
        tokenLogRepository.save(log);

        return savedToken;
    }

    public Token updateStatus(Long tokenId, String status) {

        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        String current = token.getStatus();

        if ("WAITING".equals(current) && "SERVING".equals(status)
                || "SERVING".equals(current) && "COMPLETED".equals(status)
                || "WAITING".equals(current) && "CANCELLED".equals(status)) {

            token.setStatus(status);

            if ("COMPLETED".equals(status)) {
                token.setCompletedAt(LocalDateTime.now());
            }

            Token updated = tokenRepository.save(token);

            tokenLogRepository.save(
                    new TokenLog(updated, "Status changed to " + status)
            );

            return updated;
        }

        throw new RuntimeException("Invalid status");
    }

    public Token getToken(Long tokenId) {
        return tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));
    }
}