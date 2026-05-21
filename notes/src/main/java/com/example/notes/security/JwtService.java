package com.example.notes.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Generates and validates JWT tokens.
 * 
 * JWT contains minimal claims (only username) to keep token size small.
 * This ensures token size stays well below HTTP header limits (~8KB).
 * Additional user data should be fetched from /api/auth/me endpoint if needed.
 */
@Component
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final Key key;
    private final long expirationMs;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-ms}") long expirationMs
    ) {
        if (!StringUtils.hasText(secret)) {
            throw new IllegalStateException("JWT secret is not configured.");
        }
        // Key must be at least 256 bits (32 bytes) for HS256
        if (secret.length() < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 characters.");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        log.debug("JWT key initialized successfully (HS256)");
    }

    /**
     * Generate JWT token with minimal claims.
     * Only includes username (subject) to keep token size minimal.
     * 
     * Token size is critical because:
     * - Authorization headers have size limits (~8KB in most servers)
     * - Smaller tokens = faster transfer and processing
     * - User roles can be fetched separately if needed
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                // Minimal claims - keep token small while enabling revocation by JTI
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract username from JWT token.
     */
    public String extractUsername(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    public String extractTokenId(String token) {
        return parseClaims(token).getBody().getId();
    }

    public Instant extractExpiration(String token) {
        return parseClaims(token).getBody().getExpiration().toInstant();
    }

    /**
     * Validate JWT token signature and expiration.
     */
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Parse and validate JWT claims.
     * Throws exception if token is invalid or expired.
     */
    private Jws<Claims> parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}

