package com.cardealership.docs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SwaggerDocTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void openApiJsonEndpoint_ShouldReturn200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void openApiJson_ShouldContainApiTitle() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);
        assertThat(response.getBody()).contains("\"title\":\"Car Dealership API\"");
    }

    @Test
    void openApiJson_ShouldContainApiVersion() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);
        assertThat(response.getBody()).contains("\"version\":\"1.0.0\"");
    }

    @Test
    void openApiJson_ShouldContainBearerSecurityScheme() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);
        assertThat(response.getBody()).contains("bearerAuth");
        assertThat(response.getBody()).contains("\"scheme\":\"bearer\"");
        assertThat(response.getBody()).contains("\"type\":\"http\"");
    }

    @Test
    void openApiJson_ShouldContainAuthEndpoints() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);
        assertThat(response.getBody()).contains("/api/auth/register");
        assertThat(response.getBody()).contains("/api/auth/login");
    }

    @Test
    void openApiJson_ShouldContainVehicleEndpoints() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);
        assertThat(response.getBody()).contains("/api/vehicles");
    }

    @Test
    void openApiJson_ShouldContainAuthOperationDescriptions() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);
        assertThat(response.getBody()).contains("Register a new user");
        assertThat(response.getBody()).contains("Authenticate user");
    }

    @Test
    void openApiJson_ShouldContainVehicleOperationDescriptions() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);
        assertThat(response.getBody()).contains("Get all vehicles");
        assertThat(response.getBody()).contains("Create a new vehicle");
    }

    @Test
    void openApiJson_ShouldContainSecurityRequirement() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);
        assertThat(response.getBody()).contains("\"security\"");
    }

    @Test
    void swaggerUiPage_ShouldBeAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity("/swagger-ui.html", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}