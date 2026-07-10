package com.cardealership.dto;

/**
 * DTO returned after successful authentication (login or registration).
 *
 * @param token    the JWT access token
 * @param username the authenticated user's username
 * @param role     the user's role (e.g. USER, ADMIN)
 */
public record AuthResponse(
        String token,
        String username,
        String role
) {}
