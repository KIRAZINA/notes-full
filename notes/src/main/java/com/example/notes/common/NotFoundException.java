package com.example.notes.common;

/**
 * Thrown when a resource cannot be found.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
