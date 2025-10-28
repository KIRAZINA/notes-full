package com.example.notes.web.dto;

import java.time.Instant;
import java.util.List;

/**
 * Compact read model for notes returned to clients, with tags.
 */
public record NoteResponse(
        Long id,
        String title,
        String content,
        boolean pinned,
        boolean archived,
        boolean trashed,
        Instant createdAt,
        Instant updatedAt,
        List<TagResponse> tags
) {}
