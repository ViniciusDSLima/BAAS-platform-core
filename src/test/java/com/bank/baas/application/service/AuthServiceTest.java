package com.bank.baas.application.service;

import com.bank.baas.domain.exception.AlreadyExistsException;
import com.bank.baas.domain.model.User;
import com.bank.baas.domain.repository.UserRepository;
import com.bank.baas.infrastructure.security.JwtTokenProvider;
import com.bank.baas.presentation.dto.AuthRequest;
import com.bank.baas.presentation.dto.AuthResponse;
import com.bank.baas.presentation.dto.UserRegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private UserRegistrationRequest registrationRequest;
    private AuthRequest authRequest;
    private User user;
    private String email;
    private String cpf;
    private String password;
    private String encodedPassword;
    private UUID userId;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        cpf = "12345678900";
        password = "password123";
        encodedPassword = "encodedPassword123";
        userId = UUID.randomUUID();
        jwtToken = "jwt.token.string";
        
        registrationRequest = new UserRegistrationRequest(email, cpf, password);
        authRequest = new AuthRequest(email, password);
        
        user = new User(email, cpf, encodedPassword);
        
        // Set up reflection to set the ID
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, userId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set user ID", e);
        }
    }

    @Test
    void registerUser_ShouldRegisterNewUser_WhenEmailAndCpfAreNotInUse() {
        // Arrange
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByCpf(cpf)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = authService.registerUser(registrationRequest);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(cpf, result.getCpf());
        assertEquals(encodedPassword, result.getPassword());
        verify(userRepository).existsByEmail(email);
        verify(userRepository).existsByCpf(cpf);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowAlreadyExistsException_WhenEmailIsInUse() {
        // Arrange
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            authService.registerUser(registrationRequest);
        });
        
        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).existsByCpf(cpf);
        verify(passwordEncoder, never()).encode(password);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowAlreadyExistsException_WhenCpfIsInUse() {
        // Arrange
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByCpf(cpf)).thenReturn(true);

        // Act & Assert
        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            authService.registerUser(registrationRequest);
        });
        
        assertEquals("CPF already in use", exception.getMessage());
        verify(userRepository).existsByEmail(email);
        verify(userRepository).existsByCpf(cpf);
        verify(passwordEncoder, never()).encode(password);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateUser_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        // Arrange
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.createToken(authentication)).thenReturn(jwtToken);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        // Act
        AuthResponse result = authService.authenticateUser(authRequest);

        // Assert
        assertNotNull(result);
        assertEquals(jwtToken, result.getToken());
        assertEquals(userId, result.getUserId());
        assertEquals(email, result.getEmail());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).createToken(authentication);
        verify(userRepository).findUserByEmail(email);
    }

    @Test
    void authenticateUser_ShouldThrowRuntimeException_WhenUserNotFound() {
        // Arrange
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.createToken(authentication)).thenReturn(jwtToken);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticateUser(authRequest);
        });
        
        assertEquals("User not found", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).createToken(authentication);
        verify(userRepository).findUserByEmail(email);
    }
}