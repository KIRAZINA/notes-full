package com.example.notes.audit;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Represents an audit log entry for user actions.
 */
@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who performed the action
    @Column(nullable = false)
    private Long userId;

    // Action type (CREATE_NOTE, UPDATE_NOTE, DELETE_NOTE, etc.)
    @Column(nullable = false, length = 64)
    private String action;

    // Target entity type (NOTE, TAG, USER, etc.)
    @Column(nullable = false, length = 32)
    private String entityType;

    // Target entity id
    private Long entityId;

    // Optional description
    @Column(length = 512)
    private String description;

    private Instant timestamp;
}
