package com.example.notes.web.dto;

import java.util.List;

/**
 * Unified page response for frontend convenience.
 */
public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {}
