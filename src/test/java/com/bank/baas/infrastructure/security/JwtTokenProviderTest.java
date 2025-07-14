package com.bank.baas.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private Authentication authentication;
    private UserDetails userDetails;
    private String username;
    private String token;

    @BeforeEach
    void setUp() {
        username = "test@example.com";
        userDetails = new User(username, "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        
        // Set a test validity period (1 hour)
        ReflectionTestUtils.setField(jwtTokenProvider, "validityInMilliseconds", 3600000L);
    }

    @Test
    void createToken_ShouldCreateValidToken() {
        // Act
        token = jwtTokenProvider.createToken(authentication);

        // Assert
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(username, jwtTokenProvider.getUsername(token));
    }

    @Test
    void getAuthentication_ShouldReturnValidAuthentication() {
        // Arrange
        token = jwtTokenProvider.createToken(authentication);

        // Act
        Authentication result = jwtTokenProvider.getAuthentication(token);

        // Assert
        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals(username, ((UserDetails) result.getPrincipal()).getUsername());
        
        // Verify authorities
        GrantedAuthority expectedAuthority = new SimpleGrantedAuthority("ROLE_USER");
        assertTrue(result.getAuthorities().contains(expectedAuthority));
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        // Arrange
        token = jwtTokenProvider.createToken(authentication);

        // Act & Assert
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateToken_ShouldReturnFalse_ForInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.string";

        // Act & Assert
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    void validateToken_ShouldReturnFalse_ForExpiredToken() throws Exception {
        // Arrange - Create a token with a past expiration
        // We need to access the private key field to create an expired token
        Field keyField = JwtTokenProvider.class.getDeclaredField("key");
        keyField.setAccessible(true);
        Key key = (Key) keyField.get(jwtTokenProvider);
        
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", "ROLE_USER");
        
        Date now = new Date();
        Date expiration = new Date(now.getTime() - 1000); // Expired 1 second ago
        
        String expiredToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();

        // Act & Assert
        assertFalse(jwtTokenProvider.validateToken(expiredToken));
    }

    @Test
    void getUsername_ShouldReturnUsername_FromToken() {
        // Arrange
        token = jwtTokenProvider.createToken(authentication);

        // Act
        String result = jwtTokenProvider.getUsername(token);

        // Assert
        assertEquals(username, result);
    }
}