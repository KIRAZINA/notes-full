// NoteUpdateRequest.java
package com.example.notes.web.dto;

import jakarta.validation.constraints.Size;

public record NoteUpdateRequest(
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,
        String content,
        Boolean pinned,
        Boolean archived,
        Boolean trashed
) {}
