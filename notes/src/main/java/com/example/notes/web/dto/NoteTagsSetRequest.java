package com.example.notes.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * Request to set tags of a note in batch.
 */
public record NoteTagsSetRequest(
        @NotNull(message = "Tag IDs cannot be null")
        List<@NotNull(message = "Tag ID cannot be null") @Positive(message = "Tag ID must be positive") Long> tagIds
) {}
