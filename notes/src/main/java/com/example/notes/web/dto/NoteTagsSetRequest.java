package com.example.notes.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Request to set tags of a note in batch.
 */
public record NoteTagsSetRequest(
        @NotNull(message = "Tag IDs cannot be null")
        @NotEmpty(message = "Tag IDs cannot be empty")
        List<Long> tagIds
) {}
