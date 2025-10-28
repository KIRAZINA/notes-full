package com.example.notes.audit;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for AuditLog.
 */
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
