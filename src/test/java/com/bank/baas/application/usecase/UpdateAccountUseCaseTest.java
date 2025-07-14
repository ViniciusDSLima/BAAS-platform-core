package com.bank.baas.application.usecase;

import com.bank.baas.domain.exception.AuthorizationException;
import com.bank.baas.domain.model.Account;
import com.bank.baas.domain.model.User;
import com.bank.baas.domain.repository.AccountRepository;
import com.bank.baas.domain.repository.UserRepository;
import com.bank.baas.presentation.dto.UpdateAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAccountUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private UpdateAccountUseCase updateAccountUseCase;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    private UpdateAccountRequest request;
    private String email;
    private String currentPassword;
    private String newPassword;
    private User user;
    private Account account;
    private Account updatedAccount;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        currentPassword = "password123";
        newPassword = "newpassword456";
        request = new UpdateAccountRequest(email, currentPassword, newPassword);
        
        // Create user and account
        user = new User(email, "12345678900");
        account = new Account("12345678", "0001", user, currentPassword);
        user.setAccount(account);
        
        // Create an updated account for the mock response
        updatedAccount = new Account("12345678", "0001", user, newPassword);
        
        // Set IDs and timestamps using reflection
        try {
            UUID userId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            
            // Set user ID
            java.lang.reflect.Field userIdField = User.class.getDeclaredField("id");
            userIdField.setAccessible(true);
            userIdField.set(user, userId);
            
            // Set account ID and timestamps
            java.lang.reflect.Field accountIdField = Account.class.getDeclaredField("id");
            accountIdField.setAccessible(true);
            accountIdField.set(account, accountId);
            accountIdField.set(updatedAccount, accountId);
            
            java.lang.reflect.Field createdAtField = Account.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(account, createdAt);
            createdAtField.set(updatedAccount, createdAt);
            
            java.lang.reflect.Field updatedAtField = Account.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(updatedAccount, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test data", e);
        }
    }

    @Test
    void execute_ShouldUpdateAccountPassword_WhenRequestIsValid() {
        // Arrange
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        // Act
        Account result = updateAccountUseCase.execute(request);

        // Assert
        assertNotNull(result);
        assertEquals(updatedAccount.getId(), result.getId());
        
        verify(userRepository).findUserByEmail(email);
        verify(accountRepository).save(accountCaptor.capture());
        
        Account capturedAccount = accountCaptor.getValue();
        assertNotNull(capturedAccount);
        assertEquals(account.getId(), capturedAccount.getId());
        assertNotNull(capturedAccount.getUpdatedAt());
        
        // We can't directly verify the password since it's private and there's no getter
        // But we can verify that the account was saved
    }

    @Test
    void execute_ShouldThrowAuthorizationException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            updateAccountUseCase.execute(request);
        });
        
        assertEquals("User not found: " + email, exception.getMessage());
        verify(userRepository).findUserByEmail(email);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowAuthorizationException_WhenUserHasNoAccount() {
        // Arrange
        User userWithoutAccount = new User(email, "12345678900");
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(userWithoutAccount));

        // Act & Assert
        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            updateAccountUseCase.execute(request);
        });
        
        assertEquals("User has no account", exception.getMessage());
        verify(userRepository).findUserByEmail(email);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowAuthorizationException_WhenPasswordIsIncorrect() {
        // Arrange
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        UpdateAccountRequest invalidRequest = new UpdateAccountRequest(email, "wrongpassword", newPassword);

        // Act & Assert
        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            updateAccountUseCase.execute(invalidRequest);
        });
        
        assertEquals("Invalid password", exception.getMessage());
        verify(userRepository).findUserByEmail(email);
        verify(accountRepository, never()).save(any());
    }
}