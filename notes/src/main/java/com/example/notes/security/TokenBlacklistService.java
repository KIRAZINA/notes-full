package com.example.notes.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages invalidated tokens so logout and compromise events can revoke issued JWTs.
 */
@Service
@Transactional
public class TokenBlacklistService {

    private final TokenBlacklistRepository repository;
    private final JwtService jwtService;

    public TokenBlacklistService(TokenBlacklistRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    public void blacklistToken(String token) {
        if (!StringUtils.hasText(token) || !jwtService.isValid(token)) {
            return;
        }
        String jti = jwtService.extractTokenId(token);
        if (jti == null) {
            return;
        }
        Instant expiresAt = jwtService.extractExpiration(token);
        repository.save(TokenBlacklistEntry.builder()
                .jti(jti)
                .createdAt(Instant.now())
                .expiresAt(expiresAt)
                .build());
    }

    public boolean isBlacklisted(String token) {
        if (!StringUtils.hasText(token) || !jwtService.isValid(token)) {
            return false;
        }
        String jti = jwtService.extractTokenId(token);
        if (jti == null) {
            return false;
        }
        return repository.findByJti(jti).isPresent();
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        repository.deleteExpiredBefore(Instant.now());
    }
}
