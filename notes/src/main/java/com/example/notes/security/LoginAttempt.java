package com.example.notes.security;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Tracks login attempts for brute-force protection.
 * Records failed login attempts by username to implement rate limiting.
 */
@Entity
@Table(name = "login_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String username;

    @Column(nullable = false)
    private Integer failedAttempts;

    @Column(nullable = false)
    private Instant lastAttemptTime;

    // User is locked if failedAttempts >= MAX_ATTEMPTS
    public static final int MAX_ATTEMPTS = 5;
    public static final long LOCK_DURATION_MINUTES = 15;

    public boolean isLocked() {
        return failedAttempts >= MAX_ATTEMPTS &&
                lastAttemptTime.plusSeconds(LOCK_DURATION_MINUTES * 60).isAfter(Instant.now());
    }
}
