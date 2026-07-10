package com.cardealership.service;

import com.cardealership.dto.AuthResponse;
import com.cardealership.dto.LoginRequest;
import com.cardealership.dto.RegisterRequest;
import com.cardealership.entity.User;
import com.cardealership.enums.Role;
import com.cardealership.exception.DuplicateResourceException;
import com.cardealership.repository.UserRepository;
import com.cardealership.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService, authenticationManager);
    }

    @Test
    void register_WithValidRequest_ShouldReturnAuthResponse() {
        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123");
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("test.jwt.token");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("test.jwt.token");
        assertThat(response.username()).isEqualTo("testuser");
        assertThat(response.role()).isEqualTo("USER");
    }

    @Test
    void register_WithDuplicateUsername_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest("existinguser", "test@example.com", "password123");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Username");
    }

    @Test
    void register_WithDuplicateEmail_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest("testuser", "existing@example.com", "password123");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email");
    }

    @Test
    void register_WithShortPassword_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "short");

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("8");
    }

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("test.jwt.token");

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("test.jwt.token");
        assertThat(response.username()).isEqualTo("testuser");
        assertThat(response.role()).isEqualTo("USER");
    }

    @Test
    void login_WithWrongPassword_ShouldThrowException() {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_WithNonExistentUser_ShouldThrowException() {
        LoginRequest request = new LoginRequest("nonexistent", "password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("nonexistent")).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class);
    }
}