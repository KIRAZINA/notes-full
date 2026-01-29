package com.example.notes.security;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

/**
 * Service to prevent brute-force login attacks.
 * Tracks failed login attempts and locks accounts temporarily after MAX_ATTEMPTS failures.
 */
@Service
@Transactional
public class LoginAttemptService {
    private static final Logger log = LoggerFactory.getLogger(LoginAttemptService.class);

    private final LoginAttemptRepository loginAttemptRepository;

    public LoginAttemptService(LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    /**
     * Check if account is locked due to too many failed attempts.
     */
    public boolean isAccountLocked(String username) {
        var attempt = loginAttemptRepository.findByUsername(username);
        if (attempt.isEmpty()) {
            return false;
        }
        
        LoginAttempt loginAttempt = attempt.get();
        if (loginAttempt.isLocked()) {
            log.warn("Account {} is locked due to too many failed login attempts", username);
            return true;
        }
        
        // Reset if lock expired
        if (loginAttempt.getFailedAttempts() >= LoginAttempt.MAX_ATTEMPTS &&
            !loginAttempt.isLocked()) {
            loginAttempt.setFailedAttempts(0);
            loginAttemptRepository.save(loginAttempt);
        }
        
        return false;
    }

    /**
     * Record a successful login - reset failed attempts.
     */
    public void loginSucceeded(String username) {
        var attempt = loginAttemptRepository.findByUsername(username);
        if (attempt.isPresent()) {
            LoginAttempt loginAttempt = attempt.get();
            loginAttempt.setFailedAttempts(0);
            loginAttempt.setLastAttemptTime(Instant.now());
            loginAttemptRepository.save(loginAttempt);
            log.debug("Reset failed login attempts for user {}", username);
        }
    }

    /**
     * Record a failed login attempt.
     */
    public void loginFailed(String username) {
        var attempt = loginAttemptRepository.findByUsername(username);
        
        LoginAttempt loginAttempt = attempt.orElseGet(() -> 
            LoginAttempt.builder()
                .username(username)
                .failedAttempts(0)
                .lastAttemptTime(Instant.now())
                .build()
        );
        
        loginAttempt.setFailedAttempts(loginAttempt.getFailedAttempts() + 1);
        loginAttempt.setLastAttemptTime(Instant.now());
        loginAttemptRepository.save(loginAttempt);
        
        log.warn("Failed login attempt for user {} (attempt #{})", 
            username, loginAttempt.getFailedAttempts());
        
        if (loginAttempt.isLocked()) {
            log.warn("Account {} is now locked due to too many failed attempts", username);
        }
    }

    /**
     * Manually unlock an account (admin operation).
     */
    public void unlockAccount(String username) {
        var attempt = loginAttemptRepository.findByUsername(username);
        if (attempt.isPresent()) {
            LoginAttempt loginAttempt = attempt.get();
            loginAttempt.setFailedAttempts(0);
            loginAttemptRepository.save(loginAttempt);
            log.info("Account {} was manually unlocked", username);
        }
    }
}
