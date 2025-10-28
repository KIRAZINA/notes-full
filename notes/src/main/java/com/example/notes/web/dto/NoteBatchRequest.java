package com.example.notes.web.dto;

import java.util.List;

/**
 * Request for batch operations on notes.
 */
public record NoteBatchRequest(
        List<Long> noteIds
) {}
