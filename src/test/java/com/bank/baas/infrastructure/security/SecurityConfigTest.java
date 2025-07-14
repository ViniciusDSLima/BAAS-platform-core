package com.bank.baas.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private HttpSecurity httpSecurity;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        // Act
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // Assert
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder.getClass().getName().contains("BCryptPasswordEncoder"));
    }

    @Test
    void authenticationManager_ShouldReturnAuthenticationManagerFromConfiguration() throws Exception {
        // Arrange
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        // Act
        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        // Assert
        assertNotNull(result);
        assertEquals(authenticationManager, result);
        verify(authenticationConfiguration).getAuthenticationManager();
    }

    // Note: Testing the filterChain method is challenging because it configures HttpSecurity
    // which is not easily mockable in a unit test. In a real-world scenario, this would be tested with
    // integration tests or by using Spring's testing utilities like WebMvcTest or SpringBootTest.
    //
    // For a proper test of the security configuration, you would:
    // 1. Use @WebMvcTest or @SpringBootTest to load the Spring context
    // 2. Make HTTP requests to secured and non-secured endpoints
    // 3. Verify that the security rules are applied correctly
    //
    // Since this is beyond the scope of a simple unit test, we're omitting the test for filterChain here.
    // In a real project, you would add integration tests for the security configuration.
}
