package com.example.notes.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Request for batch operations on notes.
 */
public record NoteBatchRequest(
        @NotNull(message = "Note IDs cannot be null")
        @NotEmpty(message = "Note IDs cannot be empty")
        List<@NotNull(message = "Note ID cannot be null") @Positive(message = "Note ID must be positive") Long> noteIds
) {}
