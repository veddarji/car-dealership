package com.cardealership.security;

import com.cardealership.entity.User;
import com.cardealership.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        jwtService.secretKey = "testSecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLongForHS256Algorithm";
        jwtService.expirationMs = 86400000;

        userDetails = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role(Role.USER)
                .build();
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void extractRole_ShouldReturnCorrectRole() {
        String token = jwtService.generateToken(userDetails);

        String role = jwtService.extractRole(token);

        assertThat(role).isEqualTo("USER");
    }

    @Test
    void isTokenValid_WithCorrectUser_ShouldReturnTrue() {
        String token = jwtService.generateToken(userDetails);

        boolean valid = jwtService.isTokenValid(token, userDetails);

        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValid_WithWrongUser_ShouldReturnFalse() {
        String token = jwtService.generateToken(userDetails);

        UserDetails wrongUser = User.builder()
                .username("otheruser")
                .email("other@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        boolean valid = jwtService.isTokenValid(token, wrongUser);

        assertThat(valid).isFalse();
    }
}
