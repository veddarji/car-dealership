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
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
class InventoryFlowIT {

    @Autowired
    private TestRestTemplate restTemplate;

    private String adminToken;
    private String userToken;
    private Long vehicleId;

    @BeforeEach
    void setUp() {
        String ts = String.valueOf(System.currentTimeMillis());
        ResponseEntity<AuthResponse> adminResp = restTemplate.postForEntity("/api/auth/register",
                Map.of("username", "admin-" + ts, "email", "admin-" + ts + "@test.com", "password", "password123"),
                AuthResponse.class);
        adminToken = adminResp.getBody().token();

        ResponseEntity<AuthResponse> userResp = restTemplate.postForEntity("/api/auth/register",
                Map.of("username", "user-" + ts, "email", "user-" + ts + "@test.com", "password", "password123"),
                AuthResponse.class);
        userToken = userResp.getBody().token();

        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<VehicleResponse> createResponse = restTemplate.exchange("/api/vehicles",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("make", "Toyota", "model", "Camry", "category", "Sedan",
                        "price", 27400, "quantity", 3), adminHeaders), VehicleResponse.class);
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

        for (int i = 0; i < 3; i++) {
            restTemplate.exchange("/api/vehicles/" + vehicleId + "/purchase",
                    HttpMethod.POST, new HttpEntity<>(userHeaders), VehicleResponse.class);
        }

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