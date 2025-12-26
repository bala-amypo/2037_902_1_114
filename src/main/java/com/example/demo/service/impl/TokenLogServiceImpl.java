package com.example.demo.service.impl;

import com.example.demo.entity.Token;
import com.example.demo.entity.TokenLog;
import com.example.demo.repository.TokenLogRepository;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.TokenLogService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TokenLogServiceImpl implements TokenLogService {
    
    private final TokenLogRepository logRepository;
    private final TokenRepository tokenRepository;

    public TokenLogServiceImpl(TokenLogRepository logRepository, TokenRepository tokenRepository) {
        this.logRepository = logRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public TokenLog addLog(Long tokenId, String message) {
        Token token = tokenRepository.findById(tokenId)
            .orElseThrow(() -> new RuntimeException("Token not found"));
        
        TokenLog log = new TokenLog();
        log.setToken(token);
        log.setLogMessage(message);
        log.setLoggedAt(LocalDateTime.now());
        
        return logRepository.save(log);
    }

    @Override
    public List<TokenLog> getLogs(Long tokenId) {
        return logRepository.findByToken_IdOrderByLoggedAtAsc(tokenId);
    }
}