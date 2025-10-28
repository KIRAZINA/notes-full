package com.example.notes.web.dto;

/**
 * Simple auth response carrying JWT.
 */
public record AuthResponse(
        String token
) {}
