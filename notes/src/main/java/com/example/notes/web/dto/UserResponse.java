package com.example.notes.web.dto;

/**
 * Minimal user read model (do not expose email if privacy is required).
 */
public record UserResponse(
        Long id,
        String username,
        String email
) {}
