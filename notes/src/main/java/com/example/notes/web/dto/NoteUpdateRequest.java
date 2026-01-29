// NoteUpdateRequest.java
package com.example.notes.web.dto;

import jakarta.validation.constraints.Size;

public record NoteUpdateRequest(
        @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
        String title,
        
        @Size(min = 1, message = "Content must not be empty")
        String content,
        
        Boolean pinned,
        Boolean archived,
        Boolean trashed
) {}
