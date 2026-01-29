package com.example.notes.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

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
        // Key must be at least 256 bits (32 bytes) for HS256
        if (secret.length() < 32) {
            log.warn("JWT secret is shorter than recommended 32 chars. Minimum is 32 for HS256.");
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
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                // Minimal claims - don't add roles or other data here
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract username from JWT token.
     * This is the only claim stored in token.
     */
    public String extractUsername(String token) {
        return parseClaims(token).getBody().getSubject();
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
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}

