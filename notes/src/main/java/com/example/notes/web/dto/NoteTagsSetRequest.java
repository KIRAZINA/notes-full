package com.example.notes.web.dto;

import java.util.List;

/**
 * Request to set tags of a note in batch.
 */
public record NoteTagsSetRequest(
        List<Long> tagIds
) {}
