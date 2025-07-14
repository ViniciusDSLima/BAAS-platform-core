package com.bank.baas.application.usecase;

import com.bank.baas.domain.exception.AlreadyExistsException;
import com.bank.baas.domain.model.Account;
import com.bank.baas.domain.model.User;
import com.bank.baas.domain.repository.AccountRepository;
import com.bank.baas.domain.repository.UserRepository;
import com.bank.baas.presentation.dto.CreateAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAccountUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CreateAccountUseCase createAccountUseCase;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    private CreateAccountRequest request;
    private String email;
    private String cpf;
    private String password;
    private Account savedAccount;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        cpf = "12345678900";
        password = "password123";
        request = new CreateAccountRequest(email, cpf, password);
        
        // Create a saved account for the mock response
        User user = new User(email, cpf);
        savedAccount = new Account("12345678", "0001", user, password);
        
        // Set IDs using reflection
        try {
            java.lang.reflect.Field userIdField = User.class.getDeclaredField("id");
            userIdField.setAccessible(true);
            userIdField.set(user, UUID.randomUUID());
            
            java.lang.reflect.Field accountIdField = Account.class.getDeclaredField("id");
            accountIdField.setAccessible(true);
            accountIdField.set(savedAccount, UUID.randomUUID());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test data", e);
        }
    }

    @Test
    void execute_ShouldCreateAccount_WhenRequestIsValid() {
        // Arrange
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByCpf(cpf)).thenReturn(false);
        when(accountRepository.existsByNumber(any())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        // Act
        Account result = createAccountUseCase.execute(request);

        // Assert
        assertNotNull(result);
        assertEquals(savedAccount.getId(), result.getId());
        assertEquals(savedAccount.getNumber(), result.getNumber());
        assertEquals(savedAccount.getAgency(), result.getAgency());
        
        verify(userRepository).existsByEmail(email);
        verify(userRepository).existsByCpf(cpf);
        verify(accountRepository).save(accountCaptor.capture());
        
        Account capturedAccount = accountCaptor.getValue();
        assertNotNull(capturedAccount);
        assertEquals("0001", capturedAccount.getAgency());
        
        User capturedUser = capturedAccount.getUser();
        assertNotNull(capturedUser);
        assertEquals(email, capturedUser.getEmail());
        assertEquals(cpf, capturedUser.getCpf());
    }

    @Test
    void execute_ShouldThrowAlreadyExistsException_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            createAccountUseCase.execute(request);
        });
        
        assertEquals("User with email already exists: " + email, exception.getMessage());
        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).existsByCpf(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowAlreadyExistsException_WhenCpfAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByCpf(cpf)).thenReturn(true);

        // Act & Assert
        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            createAccountUseCase.execute(request);
        });
        
        assertEquals("User with CPF already exists: " + cpf, exception.getMessage());
        verify(userRepository).existsByEmail(email);
        verify(userRepository).existsByCpf(cpf);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void execute_ShouldGenerateUniqueAccountNumber_WhenFirstNumberAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByCpf(cpf)).thenReturn(false);
        // First check returns true (number exists), second check returns false (unique number)
        when(accountRepository.existsByNumber(any())).thenReturn(true, false);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        // Act
        Account result = createAccountUseCase.execute(request);

        // Assert
        assertNotNull(result);
        verify(accountRepository, times(2)).existsByNumber(any());
        verify(accountRepository).save(any(Account.class));
    }
}