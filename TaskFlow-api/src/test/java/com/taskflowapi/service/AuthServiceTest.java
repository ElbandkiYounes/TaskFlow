package com.taskflowapi.service;

import com.taskflowapi.dto.LoginRequest;
import com.taskflowapi.dto.LoginResponse;
import com.taskflowapi.entity.User;
import com.taskflowapi.exception.UnauthorizedException;
import com.taskflowapi.repository.UserRepository;
import com.taskflowapi.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("john@example.com");
        testUser.setName("John Doe");
        testUser.setPasswordHash("$2a$10$hashedPassword");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("login() - Success: Should return LoginResponse with valid credentials")
    void login_WithValidCredentials_ShouldReturnLoginResponse() {
        // Arrange
        String expectedToken = "jwt.token.here";
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPasswordHash())).thenReturn(true);
        when(jwtUtil.generateToken(testUser.getEmail(), testUser.getId())).thenReturn(expectedToken);

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(expectedToken);
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(response.getName()).isEqualTo(testUser.getName());

        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), testUser.getPasswordHash());
        verify(jwtUtil, times(1)).generateToken(testUser.getEmail(), testUser.getId());
    }

    @Test
    @DisplayName("login() - Failure: Should throw UnauthorizedException when user not found")
    void login_WithNonExistentEmail_ShouldThrowUnauthorizedException() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid credentials");

        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("login() - Failure: Should throw UnauthorizedException when password is incorrect")
    void login_WithIncorrectPassword_ShouldThrowUnauthorizedException() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPasswordHash())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid credentials");

        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), testUser.getPasswordHash());
        verify(jwtUtil, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("login() - Edge Case: Should handle null email gracefully")
    void login_WithNullEmail_ShouldThrowUnauthorizedException() {
        // Arrange
        loginRequest.setEmail(null);
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid credentials");

        verify(userRepository, times(1)).findByEmail(null);
    }

    @Test
    @DisplayName("login() - Edge Case: Should handle empty password")
    void login_WithEmptyPassword_ShouldThrowUnauthorizedException() {
        // Arrange
        loginRequest.setPassword("");
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("", testUser.getPasswordHash())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid credentials");

        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches("", testUser.getPasswordHash());
    }

    @Test
    @DisplayName("login() - Edge Case: Should handle case-sensitive email")
    void login_WithDifferentCaseEmail_ShouldBehaveLikeNotFound() {
        // Arrange
        loginRequest.setEmail("JOHN@EXAMPLE.COM");
        when(userRepository.findByEmail("JOHN@EXAMPLE.COM")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid credentials");

        verify(userRepository, times(1)).findByEmail("JOHN@EXAMPLE.COM");
    }
}
