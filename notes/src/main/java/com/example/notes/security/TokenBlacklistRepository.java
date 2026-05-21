package com.example.notes.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.time.Instant;
import java.util.Optional;

/**
 * Repository for invalidated JWT identifiers.
 */
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklistEntry, String> {

    Optional<TokenBlacklistEntry> findByJti(String jti);

    @Modifying
    @Query("DELETE FROM TokenBlacklistEntry t WHERE t.expiresAt < ?1")
    void deleteExpiredBefore(Instant cutoff);
}
