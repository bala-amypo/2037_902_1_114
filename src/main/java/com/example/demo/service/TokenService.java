package com.example.demo.service;

import com.example.demo.entity.BreachAlert;

public interface TokenService {
    BreachAlert issueToken(Long counterId);
    BreachAlert updateStatus(Long tokenId, String status);
    BreachAlert getToken(Long tokenId);
}