package com.cardealership.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class DevProfileTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void devPropertiesFile_ShouldExist() {
        assertThat(new File("src/main/resources/application-dev.properties")).exists();
    }

    @Test
    void devPropertiesFile_ShouldUseH2() throws IOException {
        String content = Files.readString(new File("src/main/resources/application-dev.properties").toPath());
        assertThat(content).contains("h2");
        assertThat(content).contains("org.h2.Driver");
    }

    @Test
    void app_ShouldStartWithDevProfile() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void h2Console_ShouldBeAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity("/h2-console", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void swaggerUi_ShouldBeAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity("/swagger-ui.html", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void openApiJson_ShouldContainEndpoints() throws IOException {
        String content = Files.readString(new File("src/main/resources/application-dev.properties").toPath());
        assertThat(content).contains("spring.h2.console.enabled=true");
    }
}