package com.example.notes.security;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Stores invalidated JWT token IDs for logout and revocation checks.
 */
@Entity
@Table(name = "invalidated_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenBlacklistEntry {

    @Id
    @Column(name = "jti", nullable = false, length = 128)
    private String jti;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
