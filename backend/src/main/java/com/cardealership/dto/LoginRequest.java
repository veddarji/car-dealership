package com.cardealership.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user login requests.
 *
 * @param username the username
 * @param password the password
 */
public record LoginRequest(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {}
