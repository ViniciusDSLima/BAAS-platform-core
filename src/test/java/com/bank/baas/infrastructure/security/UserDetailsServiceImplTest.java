package com.bank.baas.infrastructure.security;

import com.bank.baas.domain.model.User;
import com.bank.baas.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;
    private String email;
    private String password;
    private Set<String> roles;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        password = "password123";
        roles = new HashSet<>(Arrays.asList("ROLE_USER", "ROLE_ADMIN"));

        user = new User(email, "12345678900", password);

        // Set up roles using reflection
        try {
            java.lang.reflect.Field rolesField = User.class.getDeclaredField("roles");
            rolesField.setAccessible(true);
            rolesField.set(user, roles);

            // Set up ID using reflection
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, UUID.randomUUID());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up user", e);
        }
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Arrange
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals(password, result.getPassword());

        // Verify authorities
        List<GrantedAuthority> expectedAuthorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        assertEquals(expectedAuthorities.size(), result.getAuthorities().size());
        for (GrantedAuthority expectedAuthority : expectedAuthorities) {
            assertTrue(result.getAuthorities().contains(expectedAuthority));
        }

        // Verify account status
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        assertTrue(result.isEnabled());

        verify(userRepository).findUserByEmail(email);
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        String nonExistentEmail = "nonexistent@example.com";
        when(userRepository.findUserByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(nonExistentEmail);
        });

        assertEquals("User not found with email: " + nonExistentEmail, exception.getMessage());
        verify(userRepository).findUserByEmail(nonExistentEmail);
    }
}
