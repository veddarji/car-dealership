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
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
class VehicleFlowIT {

    @Autowired
    private TestRestTemplate restTemplate;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        ResponseEntity<AuthResponse> adminResp = restTemplate.postForEntity("/api/auth/login",
                Map.of("username", "admin", "password", "admin123"),
                AuthResponse.class);
        adminToken = adminResp.getBody().token();

        String ts = String.valueOf(System.currentTimeMillis());
        ResponseEntity<AuthResponse> userResp = restTemplate.postForEntity("/api/auth/register",
                Map.of("username", "user-" + ts, "email", "user-" + ts + "@test.com", "password", "password123"),
                AuthResponse.class);
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

        restTemplate.exchange("/api/vehicles", HttpMethod.POST,
                new HttpEntity<>(Map.of("make", "BMW", "model", "X5", "category", "SUV",
                        "price", 61500, "quantity", 2), adminHeaders), VehicleResponse.class);

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

        ResponseEntity<VehicleResponse> createResponse = restTemplate.exchange("/api/vehicles",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("make", "Honda", "model", "Civic", "category", "Sedan",
                        "price", 25000, "quantity", 5), adminHeaders), VehicleResponse.class);
        Long vehicleId = createResponse.getBody().id();

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(userToken);
        userHeaders.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<VehicleResponse> purchaseResponse = restTemplate.exchange(
                "/api/vehicles/" + vehicleId + "/purchase", HttpMethod.POST,
                new HttpEntity<>(Map.of("quantity", 1), userHeaders), VehicleResponse.class);

        assertThat(purchaseResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(purchaseResponse.getBody().quantity()).isEqualTo(4);
    }

    @Test
    void unauthorizedUserCannotDeleteVehicle() {
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<VehicleResponse> createResponse = restTemplate.exchange("/api/vehicles",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("make", "Ford", "model", "Mustang", "category", "Coupe",
                        "price", 43200, "quantity", 2), adminHeaders), VehicleResponse.class);
        Long vehicleId = createResponse.getBody().id();

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(userToken);

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/vehicles/" + vehicleId, HttpMethod.DELETE,
                new HttpEntity<>(userHeaders), Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}