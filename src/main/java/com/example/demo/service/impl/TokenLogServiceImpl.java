ackage com.example.demo.service.impl;

import com.example.demo.entity.TokenLog;
import com.example.demo.repository.TokenLogRepository;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.TokenLogService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TokenLogServiceImpl implements TokenLogService {
    
    private final TokenLogRepository tokenLogRepository;
    private final TokenRepository tokenRepository;
    
    public TokenLogServiceImpl(TokenLogRepository tokenLogRepository, TokenRepository tokenRepository) {
        this.tokenLogRepository = tokenLogRepository;
        this.tokenRepository = tokenRepository;
    }
    
    @Override
    public TokenLog addLog(Long tokenId, String message) {
        var token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        
        TokenLog log = new TokenLog(token, message, LocalDateTime.now());
        return tokenLogRepository.save(log);
    }
    
    @Override
    public List<TokenLog> getLogs(Long tokenId) {
        return tokenLogRepository.findByTokenIdOrderByLoggedAtAsc(tokenId);
    }
}
