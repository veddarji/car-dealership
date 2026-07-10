package com.cardealership.integration;

import com.cardealership.dto.AuthResponse;
import com.cardealership.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AuthFlowIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void registerUser_ThenLogin_ShouldReturnToken() {
        Map<String, String> registerBody = Map.of(
                "username", "newuser",
                "email", "newuser@example.com",
                "password", "password123");

        ResponseEntity<AuthResponse> registerResponse = restTemplate.postForEntity(
                "/api/auth/register", registerBody, AuthResponse.class);

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerResponse.getBody()).isNotNull();
        assertThat(registerResponse.getBody().token()).isNotBlank();
        assertThat(registerResponse.getBody().username()).isEqualTo("newuser");
        assertThat(registerResponse.getBody().role()).isEqualTo("USER");

        Map<String, String> loginBody = Map.of(
                "username", "newuser",
                "password", "password123");

        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
                "/api/auth/login", loginBody, AuthResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().token()).isNotBlank();
    }

    @Test
    void registerDuplicateUser_ShouldReturn409() {
        Map<String, String> body = Map.of(
                "username", "dupuser",
                "email", "dup@example.com",
                "password", "password123");

        restTemplate.postForEntity("/api/auth/register", body, AuthResponse.class);

        ResponseEntity<ErrorResponse> duplicateResponse = restTemplate.postForEntity(
                "/api/auth/register", body, ErrorResponse.class);

        assertThat(duplicateResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void loginWithWrongPassword_ShouldReturn401() {
        Map<String, String> body = Map.of(
                "username", "nonexistent",
                "password", "wrongpass");

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/api/auth/login", body, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}