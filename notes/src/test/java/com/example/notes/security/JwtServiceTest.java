package com.example.notes.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private final String secret = "my-super-secret-key-my-super-secret-key"; // >= 32 chars
    private final long expirationMs = 3600000; // 1 hour

    private final JwtService jwtService = new JwtService(secret, expirationMs);

    @Test
    void generateToken_and_extractUsername_shouldWork() {
        String token = jwtService.generateToken("john");

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("john");
    }

    @Test
    void isValid_shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken("john");

        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test
    void isValid_shouldReturnFalseForInvalidToken() {
        assertThat(jwtService.isValid("invalid.token.value")).isFalse();
    }
}
