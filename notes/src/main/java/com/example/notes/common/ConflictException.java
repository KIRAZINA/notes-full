package com.example.notes.common;

/**
 * Thrown when a state conflicts with existing data (e.g., duplicates).
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
