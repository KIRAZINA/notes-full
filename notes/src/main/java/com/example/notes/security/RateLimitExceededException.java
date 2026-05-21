package com.example.notes.security;

import com.example.notes.common.ConflictException;

/**
 * Thrown when a request exceeds configured rate limits.
 */
public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}
