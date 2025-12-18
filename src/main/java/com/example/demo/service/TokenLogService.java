package com.example.demo.service;

import com.example.demo.entity.Token;
import com.example.demo.entity.TokenLog;
import com.example.demo.repository.TokenLogRepository;
import com.example.demo.repository.TokenRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenLogService {

    private final TokenLogRepository tokenLogRepository;
    private final TokenRepository tokenRepository;

    public TokenLogService(TokenLogRepository tokenLogRepository,
                           TokenRepository tokenRepository) {
        this.tokenLogRepository = tokenLogRepository;
        this.tokenRepository = tokenRepository;
    }

    public TokenLog addLog(Long tokenId, String message) {

        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        TokenLog log = new TokenLog(token, message);
        return tokenLogRepository.save(log);
    }

    public List<TokenLog> getLogs(Long tokenId) {
        return tokenLogRepository.findByToken_IdOrderByLoggedAtAsc(tokenId);
    }
}