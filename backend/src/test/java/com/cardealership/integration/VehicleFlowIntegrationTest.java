package com.cardealership.integration;

import com.cardealership.dto.AuthResponse;
import com.cardealership.dto.PagedResponse;
import com.cardealership.dto.VehicleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
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
class VehicleFlowIntegrationTest {

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

    @BeforeEach
    void setUp() {
        Map<String, String> adminBody = Map.of(
                "username", "admin-flow-" + System.currentTimeMillis(),
                "email", "admin-flow@test.com",
                "password", "password123");
        ResponseEntity<AuthResponse> adminResp = restTemplate.postForEntity(
                "/api/auth/register", adminBody, AuthResponse.class);
        adminToken = adminResp.getBody().token();

        Map<String, String> userBody = Map.of(
                "username", "user-flow-" + System.currentTimeMillis(),
                "email", "user-flow@test.com",
                "password", "password123");
        ResponseEntity<AuthResponse> userResp = restTemplate.postForEntity(
                "/api/auth/register", userBody, AuthResponse.class);
        userToken = userResp.getBody().token();
    }

    @Test
    void adminCreatesVehicle_ThenUserViewsIt() {
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> createRequest = new HttpEntity<>(Map.of(
                "make", "Tesla", "model", "Model 3", "category", "Sedan",
                "price", 41900, "quantity", 3), adminHeaders);

        ResponseEntity<VehicleResponse> createResponse = restTemplate.exchange(
                "/api/vehicles", HttpMethod.POST, createRequest, VehicleResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody().id()).isNotNull();
        Long vehicleId = createResponse.getBody().id();

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(userToken);

        ResponseEntity<VehicleResponse> getResponse = restTemplate.exchange(
                "/api/vehicles/" + vehicleId, HttpMethod.GET,
                new HttpEntity<>(userHeaders), VehicleResponse.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().make()).isEqualTo("Tesla");
    }

    @Test
    void searchVehicles_WithMultipleFilters_ReturnsCorrectResults() {
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> createRequest = new HttpEntity<>(Map.of(
                "make", "BMW", "model", "X5", "category", "SUV",
                "price", 61500, "quantity", 2), adminHeaders);

        restTemplate.exchange("/api/vehicles", HttpMethod.POST, createRequest, VehicleResponse.class);

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(userToken);

        ResponseEntity<PagedResponse<VehicleResponse>> searchResponse = restTemplate.exchange(
                "/api/vehicles/search?make=BMW&category=SUV", HttpMethod.GET,
                new HttpEntity<>(userHeaders),
                new ParameterizedTypeReference<PagedResponse<VehicleResponse>>() {});

        assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchResponse.getBody().content()).isNotEmpty();
        assertThat(searchResponse.getBody().content().get(0).make()).isEqualTo("BMW");
    }

    @Test
    void purchaseVehicle_DecreasesStock() {
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> createRequest = new HttpEntity<>(Map.of(
                "make", "Honda", "model", "Civic", "category", "Sedan",
                "price", 25000, "quantity", 5), adminHeaders);

        ResponseEntity<VehicleResponse> createResponse = restTemplate.exchange(
                "/api/vehicles", HttpMethod.POST, createRequest, VehicleResponse.class);
        Long vehicleId = createResponse.getBody().id();

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(userToken);

        ResponseEntity<VehicleResponse> purchaseResponse = restTemplate.exchange(
                "/api/vehicles/" + vehicleId + "/purchase", HttpMethod.POST,
                new HttpEntity<>(userHeaders), VehicleResponse.class);

        assertThat(purchaseResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(purchaseResponse.getBody().quantity()).isEqualTo(4);
    }

    @Test
    void unauthorizedUserCannotDeleteVehicle() {
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> createRequest = new HttpEntity<>(Map.of(
                "make", "Ford", "model", "Mustang", "category", "Coupe",
                "price", 43200, "quantity", 2), adminHeaders);

        ResponseEntity<VehicleResponse> createResponse = restTemplate.exchange(
                "/api/vehicles", HttpMethod.POST, createRequest, VehicleResponse.class);
        Long vehicleId = createResponse.getBody().id();

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(userToken);

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/vehicles/" + vehicleId, HttpMethod.DELETE,
                new HttpEntity<>(userHeaders), Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}