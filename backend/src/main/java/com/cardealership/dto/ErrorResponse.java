package com.cardealership.dto;

import java.time.LocalDateTime;

/**
 * Standard error response DTO returned by the global exception handler.
 *
 * @param status    the HTTP status code
 * @param message   a human-readable error message
 * @param timestamp the time the error occurred
 * @param errors    optional map of field-level validation errors
 */
public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp,
        java.util.Map<String, String> errors
) {
    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this(status, message, timestamp, null);
    }
}
