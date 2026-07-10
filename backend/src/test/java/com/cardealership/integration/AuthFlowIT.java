package com.cardealership.integration;

import com.cardealership.dto.AuthResponse;
import com.cardealership.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
class AuthFlowIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void registerUser_ThenLogin_ShouldReturnToken() {
        String ts = String.valueOf(System.currentTimeMillis());
        Map<String, String> body = Map.of(
                "username", "user-" + ts, "email", ts + "@test.com", "password", "password123");

        ResponseEntity<AuthResponse> registerResponse = restTemplate.postForEntity(
                "/api/auth/register", body, AuthResponse.class);

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerResponse.getBody()).isNotNull();
        assertThat(registerResponse.getBody().token()).isNotBlank();

        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
                "/api/auth/login", Map.of("username", "user-" + ts, "password", "password123"),
                AuthResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().token()).isNotBlank();
    }

    @Test
    void registerDuplicateUser_ShouldReturn409() {
        String ts = String.valueOf(System.currentTimeMillis());
        Map<String, String> body = Map.of(
                "username", "dup-" + ts, "email", "dup-" + ts + "@test.com", "password", "password123");

        restTemplate.postForEntity("/api/auth/register", body, AuthResponse.class);

        ResponseEntity<ErrorResponse> duplicateResponse = restTemplate.postForEntity(
                "/api/auth/register", body, ErrorResponse.class);

        assertThat(duplicateResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void loginWithWrongPassword_ShouldReturn401() {
        Map<String, String> body = Map.of("username", "nobody", "password", "wrongpass");

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/api/auth/login", body, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}