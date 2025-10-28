package com.example.notes.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for user registration with basic validation.
 */
public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 32) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 128) String password
) {}
