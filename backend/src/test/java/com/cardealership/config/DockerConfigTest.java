package com.cardealership.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class DockerConfigTest {

    private static final File BACKEND_DIR = new File("").getAbsoluteFile();
    private static final File PROJECT_ROOT = BACKEND_DIR.getParentFile();

    @Test
    void dockerComposeFile_ShouldExist() {
        assertThat(new File(BACKEND_DIR, "docker-compose.yml")).exists();
    }

    @Test
    void dockerComposeFile_ShouldDefinePostgresService() throws IOException {
        File file = new File(BACKEND_DIR, "docker-compose.yml");
        String content = Files.readString(file.toPath());
        assertThat(content).contains("postgres");
        assertThat(content).contains("image: postgres:16");
    }

    @Test
    void dockerComposeFile_ShouldDefineAppService() throws IOException {
        File file = new File(BACKEND_DIR, "docker-compose.yml");
        String content = Files.readString(file.toPath());
        assertThat(content).contains("app:");
    }

    @Test
    void dockerComposeFile_ShouldHaveBuildContext() throws IOException {
        File file = new File(BACKEND_DIR, "docker-compose.yml");
        String content = Files.readString(file.toPath());
        assertThat(content).contains("build");
        assertThat(content).contains(".env");
    }

    @Test
    void dockerComposeFile_ShouldExposeCorrectPort() throws IOException {
        File file = new File(BACKEND_DIR, "docker-compose.yml");
        String content = Files.readString(file.toPath());
        assertThat(content).contains(":8080");
    }

    @Test
    void envExampleFile_ShouldExist() {
        assertThat(new File(BACKEND_DIR, ".env.example")).exists();
    }

    @Test
    void envExampleFile_ShouldContainRequiredVariables() throws IOException {
        File file = new File(BACKEND_DIR, ".env.example");
        String content = Files.readString(file.toPath());
        assertThat(content).contains("POSTGRES_DB");
        assertThat(content).contains("POSTGRES_USER");
        assertThat(content).contains("POSTGRES_PASSWORD");
        assertThat(content).contains("JWT_SECRET");
    }

    @Test
    void frontendEnvExample_ShouldExist() throws IOException {
        File frontendEnv = new File(PROJECT_ROOT, "frontend/.env.example");
        assertThat(frontendEnv).exists();
        String content = Files.readString(frontendEnv.toPath());
        assertThat(content).contains("VITE_API_URL");
    }

    @Test
    void applicationProperties_ShouldUsePlaceholders() throws IOException {
        File props = new File(BACKEND_DIR, "src/main/resources/application.properties");
        String content = Files.readString(props.toPath());
        assertThat(content).contains("SERVER_PORT:");
        assertThat(content).contains("DB_HOST:");
        assertThat(content).contains("DB_PORT:");
        assertThat(content).contains("DB_NAME:");
        assertThat(content).contains("DB_USERNAME:");
        assertThat(content).contains("DB_PASSWORD:");
        assertThat(content).contains("JWT_SECRET:");
        assertThat(content).contains("JWT_EXPIRATION_MS:");
    }

    @Test
    void gitignore_ShouldExcludeEnvFiles() throws IOException {
        File gitignore = new File(PROJECT_ROOT, ".gitignore");
        String content = Files.readString(gitignore.toPath());
        assertThat(content).contains("backend/.env");
        assertThat(content).contains("frontend/.env");
    }
}