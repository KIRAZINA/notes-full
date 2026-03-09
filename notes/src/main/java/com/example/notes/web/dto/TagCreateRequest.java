package com.example.notes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request for creating a tag.
 */
public record TagCreateRequest(
        @NotBlank(message = "Tag name is required")
        @Size(max = 64, message = "Tag name must not exceed 64 characters")
        String name
) {}
