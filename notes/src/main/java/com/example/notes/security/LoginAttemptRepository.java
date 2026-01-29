package com.example.notes.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.Instant;
import java.util.Optional;

/**
 * Repository for LoginAttempt tracking.
 */
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    Optional<LoginAttempt> findByUsername(String username);
    
    @Query("DELETE FROM LoginAttempt la WHERE la.lastAttemptTime < ?1")
    void deleteOldAttempts(Instant cutoffTime);
}
