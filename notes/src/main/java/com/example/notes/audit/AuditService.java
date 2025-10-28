package com.example.notes.audit;

import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service to record audit events.
 */
@Service
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void record(Long userId, String action, String entityType, Long entityId, String description) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .description(description)
                .timestamp(Instant.now())
                .build();
        repository.save(log);
    }
}
