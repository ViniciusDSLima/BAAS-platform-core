package com.bank.baas.application.usecase;

import com.bank.baas.domain.exception.AuthorizationException;
import com.bank.baas.domain.model.Account;
import com.bank.baas.domain.model.DepositCode;
import com.bank.baas.domain.model.User;
import com.bank.baas.domain.repository.DepositCodeRepository;
import com.bank.baas.domain.repository.UserRepository;
import com.bank.baas.presentation.dto.GenerateDepositCodeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerateDepositCodeUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepositCodeRepository depositCodeRepository;

    @InjectMocks
    private GenerateDepositCodeUseCase generateDepositCodeUseCase;

    @Captor
    private ArgumentCaptor<DepositCode> depositCodeCaptor;

    private GenerateDepositCodeRequest request;
    private String email;
    private String password;
    private BigDecimal amount;
    private User user;
    private Account account;
    private DepositCode savedDepositCode;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        password = "password123";
        amount = new BigDecimal("100.00");
        request = new GenerateDepositCodeRequest(email, password, amount);

        // Create user and account
        user = new User(email, "12345678900");
        account = new Account("12345678", "0001", user, password);
        user.setAccount(account);

        // Create a saved deposit code for the mock response
        savedDepositCode = new DepositCode("ABCD1234", amount, user);

        // Set IDs using reflection
        try {
            java.lang.reflect.Field userIdField = User.class.getDeclaredField("id");
            userIdField.setAccessible(true);
            userIdField.set(user, UUID.randomUUID());

            java.lang.reflect.Field accountIdField = Account.class.getDeclaredField("id");
            accountIdField.setAccessible(true);
            accountIdField.set(account, UUID.randomUUID());

            java.lang.reflect.Field depositCodeIdField = DepositCode.class.getDeclaredField("id");
            depositCodeIdField.setAccessible(true);
            depositCodeIdField.set(savedDepositCode, UUID.randomUUID());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test data", e);
        }
    }

    @Test
    void execute_ShouldGenerateDepositCode_WhenRequestIsValid() {
        // Arrange
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(depositCodeRepository.existsByCode(any())).thenReturn(false);
        when(depositCodeRepository.save(any(DepositCode.class))).thenReturn(savedDepositCode);

        // Act
        DepositCode result = generateDepositCodeUseCase.execute(request);

        // Assert
        assertNotNull(result);
        assertEquals(savedDepositCode.getId(), result.getId());
        assertEquals(savedDepositCode.getCode(), result.getCode());
        assertEquals(savedDepositCode.getAmount(), result.getAmount());

        verify(userRepository).findUserByEmail(email);
        verify(depositCodeRepository).existsByCode(any());
        verify(depositCodeRepository).save(depositCodeCaptor.capture());

        DepositCode capturedDepositCode = depositCodeCaptor.getValue();
        assertNotNull(capturedDepositCode);
        assertEquals(amount, capturedDepositCode.getAmount());
        assertEquals(user, capturedDepositCode.getGenerator());
    }

    @Test
    void execute_ShouldThrowIllegalArgumentException_WhenAmountIsZero() {
        // Arrange
        GenerateDepositCodeRequest invalidRequest = new GenerateDepositCodeRequest(email, password, BigDecimal.ZERO);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            generateDepositCodeUseCase.execute(invalidRequest);
        });

        assertEquals("Amount must be greater than zero", exception.getMessage());
        verify(userRepository, never()).findUserByEmail(any());
        verify(depositCodeRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowIllegalArgumentException_WhenAmountIsNegative() {
        // Arrange
        GenerateDepositCodeRequest invalidRequest = new GenerateDepositCodeRequest(email, password, new BigDecimal("-10.00"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            generateDepositCodeUseCase.execute(invalidRequest);
        });

        assertEquals("Amount must be greater than zero", exception.getMessage());
        verify(userRepository, never()).findUserByEmail(any());
        verify(depositCodeRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowAuthorizationException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            generateDepositCodeUseCase.execute(request);
        });

        assertEquals("User not found: " + email, exception.getMessage());
        verify(userRepository).findUserByEmail(email);
        verify(depositCodeRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowAuthorizationException_WhenUserHasNoAccount() {
        // Arrange
        User userWithoutAccount = new User(email, "12345678900");
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(userWithoutAccount));

        // Act & Assert
        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            generateDepositCodeUseCase.execute(request);
        });

        assertEquals("User has no account", exception.getMessage());
        verify(userRepository).findUserByEmail(email);
        verify(depositCodeRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowAuthorizationException_WhenPasswordIsIncorrect() {
        // Arrange
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        GenerateDepositCodeRequest invalidRequest = new GenerateDepositCodeRequest(email, "wrongpassword", amount);

        // Act & Assert
        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            generateDepositCodeUseCase.execute(invalidRequest);
        });

        assertEquals("Invalid password", exception.getMessage());
        verify(userRepository).findUserByEmail(email);
        verify(depositCodeRepository, never()).save(any());
    }

    @Test
    void execute_ShouldGenerateUniqueCode_WhenFirstCodeAlreadyExists() {
        // Arrange
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        // First check returns true (code exists), second check returns false (unique code)
        when(depositCodeRepository.existsByCode(any())).thenReturn(true, false);
        when(depositCodeRepository.save(any(DepositCode.class))).thenReturn(savedDepositCode);

        // Act
        DepositCode result = generateDepositCodeUseCase.execute(request);

        // Assert
        assertNotNull(result);
        verify(depositCodeRepository, times(2)).existsByCode(any());
        verify(depositCodeRepository).save(any(DepositCode.class));
    }
}
