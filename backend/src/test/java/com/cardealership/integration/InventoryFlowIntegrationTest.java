package com.cardealership.integration;

import com.cardealership.dto.AuthResponse;
import com.cardealership.dto.ErrorResponse;
import com.cardealership.dto.VehicleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class InventoryFlowIntegrationTest {

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

    private String adminToken;
    private String userToken;
    private Long vehicleId;

    @BeforeEach
    void setUp() {
        Map<String, String> adminBody = Map.of(
                "username", "admin-inv-" + System.currentTimeMillis(),
                "email", "admin-inv@test.com",
                "password", "password123");
        ResponseEntity<AuthResponse> adminResp = restTemplate.postForEntity(
                "/api/auth/register", adminBody, AuthResponse.class);
        adminToken = adminResp.getBody().token();

        Map<String, String> userBody = Map.of(
                "username", "user-inv-" + System.currentTimeMillis(),
                "email", "user-inv@test.com",
                "password", "password123");
        ResponseEntity<AuthResponse> userResp = restTemplate.postForEntity(
                "/api/auth/register", userBody, AuthResponse.class);
        userToken = userResp.getBody().token();

        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> createRequest = new HttpEntity<>(Map.of(
                "make", "Toyota", "model", "Camry", "category", "Sedan",
                "price", 27400, "quantity", 3), adminHeaders);

        ResponseEntity<VehicleResponse> createResponse = restTemplate.exchange(
                "/api/vehicles", HttpMethod.POST, createRequest, VehicleResponse.class);
        vehicleId = createResponse.getBody().id();
    }

    @Test
    void purchaseVehicle_WithSufficientStock_Success() {
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(userToken);

        ResponseEntity<VehicleResponse> response = restTemplate.exchange(
                "/api/vehicles/" + vehicleId + "/purchase", HttpMethod.POST,
                new HttpEntity<>(userHeaders), VehicleResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().quantity()).isEqualTo(2);
    }

    @Test
    void purchaseVehicle_WithInsufficientStock_Returns400() {
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(userToken);

        restTemplate.exchange("/api/vehicles/" + vehicleId + "/purchase",
                HttpMethod.POST, new HttpEntity<>(userHeaders), VehicleResponse.class);
        restTemplate.exchange("/api/vehicles/" + vehicleId + "/purchase",
                HttpMethod.POST, new HttpEntity<>(userHeaders), VehicleResponse.class);
        restTemplate.exchange("/api/vehicles/" + vehicleId + "/purchase",
                HttpMethod.POST, new HttpEntity<>(userHeaders), VehicleResponse.class);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/api/vehicles/" + vehicleId + "/purchase", HttpMethod.POST,
                new HttpEntity<>(userHeaders), ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void restockVehicle_AsAdmin_Success() {
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Integer>> restockRequest = new HttpEntity<>(
                Map.of("quantity", 5), adminHeaders);

        ResponseEntity<VehicleResponse> response = restTemplate.exchange(
                "/api/vehicles/" + vehicleId + "/restock", HttpMethod.POST,
                restockRequest, VehicleResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().quantity()).isEqualTo(8);
    }

    @Test
    void restockVehicle_AsUser_Returns403() {
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(userToken);
        userHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Integer>> restockRequest = new HttpEntity<>(
                Map.of("quantity", 5), userHeaders);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/api/vehicles/" + vehicleId + "/restock", HttpMethod.POST,
                restockRequest, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}