// NoteCreateRequest.java
package com.example.notes.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoteCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        @NotBlank(message = "Content is required")
        String content
) {}
