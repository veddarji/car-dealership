package com.cardealership.dto;

import java.time.LocalDateTime;

/**
 * Standard error response DTO returned by the global exception handler.
 *
 * @param status    the HTTP status code
 * @param message   a human-readable error message
 * @param timestamp the time the error occurred
 */
public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {}
