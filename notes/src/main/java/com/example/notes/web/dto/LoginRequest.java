package com.example.notes.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for login with minimal validation.
 */
public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {}
