package com.bank.baas.application.usecase;

import com.bank.baas.domain.enums.TransactionStatus;
import com.bank.baas.domain.exception.AuthorizationException;
import com.bank.baas.domain.model.Account;
import com.bank.baas.domain.model.Transaction;
import com.bank.baas.domain.model.User;
import com.bank.baas.domain.repository.AccountRepository;
import com.bank.baas.domain.repository.TransactionRepository;
import com.bank.baas.domain.repository.UserRepository;
import com.bank.baas.presentation.dto.UserTransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTransactionUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private UserTransactionUseCase userTransactionUseCase;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    private UserTransactionRequest request;
    private String senderEmail;
    private String receiverEmail;
    private String password;
    private BigDecimal amount;
    private User sender;
    private User receiver;
    private Account senderAccount;
    private Account receiverAccount;
    private Transaction savedTransaction;

    @BeforeEach
    void setUp() {
        senderEmail = "sender@example.com";
        receiverEmail = "receiver@example.com";
        password = "password123";
        amount = new BigDecimal("100.00");
        request = new UserTransactionRequest(senderEmail, receiverEmail, amount, password, false);

        // Create users and accounts
        sender = new User(senderEmail, "12345678900");
        receiver = new User(receiverEmail, "98765432100");

        senderAccount = new Account("12345678", "0001", sender, password);
        receiverAccount = new Account("87654321", "0001", receiver, "receiverpassword");

        // Set initial balances
        try {
            java.lang.reflect.Field balanceField = Account.class.getDeclaredField("balance");
            balanceField.setAccessible(true);
            balanceField.set(senderAccount, new BigDecimal("500.00"));
            balanceField.set(receiverAccount, new BigDecimal("200.00"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to set account balances", e);
        }

        sender.setAccount(senderAccount);
        receiver.setAccount(receiverAccount);

        // Create a saved transaction for the mock response
        savedTransaction = new Transaction(sender, receiver, amount, LocalDateTime.now());
        savedTransaction.setStatus(TransactionStatus.SUCCESS);

        // Set IDs using reflection
        try {
            UUID senderId = UUID.randomUUID();
            UUID receiverId = UUID.randomUUID();
            UUID senderAccountId = UUID.randomUUID();
            UUID receiverAccountId = UUID.randomUUID();
            UUID transactionId = UUID.randomUUID();

            // Set user IDs
            java.lang.reflect.Field userIdField = User.class.getDeclaredField("id");
            userIdField.setAccessible(true);
            userIdField.set(sender, senderId);
            userIdField.set(receiver, receiverId);

            // Set account IDs
            java.lang.reflect.Field accountIdField = Account.class.getDeclaredField("id");
            accountIdField.setAccessible(true);
            accountIdField.set(senderAccount, senderAccountId);
            accountIdField.set(receiverAccount, receiverAccountId);

            // Set transaction ID
            java.lang.reflect.Field transactionIdField = Transaction.class.getDeclaredField("id");
            transactionIdField.setAccessible(true);
            transactionIdField.set(savedTransaction, transactionId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test data", e);
        }
    }

    @Test
    void execute_ShouldCompleteTransaction_WhenRequestIsValid() {
        // Arrange
        when(userRepository.findUserByEmail(senderEmail)).thenReturn(Optional.of(sender));
        when(userRepository.findUserByEmail(receiverEmail)).thenReturn(Optional.of(receiver));
        when(accountRepository.save(any(Account.class))).thenReturn(senderAccount, receiverAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // Act
        Transaction result = userTransactionUseCase.execute(request);

        // Assert
        assertNotNull(result);
        assertEquals(savedTransaction.getId(), result.getId());
        assertEquals(TransactionStatus.SUCCESS, result.getStatus());

        verify(userRepository).findUserByEmail(senderEmail);
        verify(userRepository).findUserByEmail(receiverEmail);
        verify(accountRepository, times(2)).save(accountCaptor.capture());
        verify(transactionRepository).save(transactionCaptor.capture());

        // Verify the accounts were updated correctly
        assertEquals(2, accountCaptor.getAllValues().size());
        Account capturedSenderAccount = accountCaptor.getAllValues().get(0);
        Account capturedReceiverAccount = accountCaptor.getAllValues().get(1);

        assertEquals(new BigDecimal("400.00"), capturedSenderAccount.getBalance());
        assertEquals(new BigDecimal("300.00"), capturedReceiverAccount.getBalance());

        // Verify the transaction was created correctly
        Transaction capturedTransaction = transactionCaptor.getValue();
        assertEquals(sender, capturedTransaction.getSender());
        assertEquals(receiver, capturedTransaction.getReceiver());
        assertEquals(amount, capturedTransaction.getAmount());
    }

    @Test
    void execute_ShouldFindUsersByCpf_WhenIsCpfIsTrue() {
        // Arrange
        String senderCpf = "12345678900";
        String receiverCpf = "98765432100";
        UserTransactionRequest cpfRequest = new UserTransactionRequest(senderCpf, receiverCpf, amount, password, true);

        when(userRepository.findUserByCPF(senderCpf)).thenReturn(Optional.of(sender));
        when(userRepository.findUserByCPF(receiverCpf)).thenReturn(Optional.of(receiver));
        when(accountRepository.save(any(Account.class))).thenReturn(senderAccount, receiverAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // Act
        Transaction result = userTransactionUseCase.execute(cpfRequest);

        // Assert
        assertNotNull(result);
        verify(userRepository).findUserByCPF(senderCpf);
        verify(userRepository).findUserByCPF(receiverCpf);
        verify(userRepository, never()).findUserByEmail(any());
    }

    @Test
    void execute_ShouldThrowAuthorizationException_WhenSenderNotFound() {
        // Arrange
        when(userRepository.findUserByEmail(senderEmail)).thenReturn(Optional.empty());

        // Act & Assert
        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            userTransactionUseCase.execute(request);
        });

        assertEquals("User not found: " + senderEmail, exception.getMessage());
        verify(userRepository).findUserByEmail(senderEmail);
        verify(userRepository, never()).findUserByEmail(receiverEmail);
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowAuthorizationException_WhenReceiverNotFound() {
        // Arrange
        when(userRepository.findUserByEmail(senderEmail)).thenReturn(Optional.of(sender));
        when(userRepository.findUserByEmail(receiverEmail)).thenReturn(Optional.empty());

        // Act & Assert
        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            userTransactionUseCase.execute(request);
        });

        assertEquals("User not found: " + receiverEmail, exception.getMessage());
        verify(userRepository).findUserByEmail(senderEmail);
        verify(userRepository).findUserByEmail(receiverEmail);
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowAuthorizationException_WhenSenderHasNoAccount() {
        // Arrange
        User senderWithoutAccount = new User(senderEmail, "12345678900");
        when(userRepository.findUserByEmail(senderEmail)).thenReturn(Optional.of(senderWithoutAccount));

        // Act & Assert
        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            userTransactionUseCase.execute(request);
        });

        assertEquals("Sender has no account", exception.getMessage());
        verify(userRepository).findUserByEmail(senderEmail);
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowAuthorizationException_WhenReceiverHasNoAccount() {
        // Arrange
        User receiverWithoutAccount = new User(receiverEmail, "98765432100");
        when(userRepository.findUserByEmail(senderEmail)).thenReturn(Optional.of(sender));
        when(userRepository.findUserByEmail(receiverEmail)).thenReturn(Optional.of(receiverWithoutAccount));

        // Act & Assert
        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            userTransactionUseCase.execute(request);
        });

        assertEquals("Receiver has no account", exception.getMessage());
        verify(userRepository).findUserByEmail(senderEmail);
        verify(userRepository).findUserByEmail(receiverEmail);
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowAuthorizationException_WhenPasswordIsIncorrect() {
        // Arrange
        when(userRepository.findUserByEmail(senderEmail)).thenReturn(Optional.of(sender));
        UserTransactionRequest invalidRequest = new UserTransactionRequest(senderEmail, receiverEmail, amount, "wrongpassword", false);

        // Act & Assert
        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            userTransactionUseCase.execute(invalidRequest);
        });

        assertEquals("Invalid password", exception.getMessage());
        verify(userRepository).findUserByEmail(senderEmail);
        verify(userRepository, never()).findUserByEmail(receiverEmail);
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowAuthorizationException_WhenAmountIsZero() {
        // Arrange
        when(userRepository.findUserByEmail(senderEmail)).thenReturn(Optional.of(sender));
        when(userRepository.findUserByEmail(receiverEmail)).thenReturn(Optional.of(receiver));
        UserTransactionRequest invalidRequest = new UserTransactionRequest(senderEmail, receiverEmail, BigDecimal.ZERO, password, false);

        // Act & Assert
        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            userTransactionUseCase.execute(invalidRequest);
        });

        assertEquals("Amount must be greater than zero", exception.getMessage());
        verify(userRepository).findUserByEmail(senderEmail);
        verify(userRepository).findUserByEmail(receiverEmail);
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowAuthorizationException_WhenAmountIsNegative() {
        // Arrange
        when(userRepository.findUserByEmail(senderEmail)).thenReturn(Optional.of(sender));
        when(userRepository.findUserByEmail(receiverEmail)).thenReturn(Optional.of(receiver));
        UserTransactionRequest invalidRequest = new UserTransactionRequest(senderEmail, receiverEmail, new BigDecimal("-10.00"), password, false);

        // Act & Assert
        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            userTransactionUseCase.execute(invalidRequest);
        });

        assertEquals("Amount must be greater than zero", exception.getMessage());
        verify(userRepository).findUserByEmail(senderEmail);
        verify(userRepository).findUserByEmail(receiverEmail);
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowAuthorizationException_WhenSenderHasInsufficientBalance() {
        // Arrange
        when(userRepository.findUserByEmail(senderEmail)).thenReturn(Optional.of(sender));
        when(userRepository.findUserByEmail(receiverEmail)).thenReturn(Optional.of(receiver));
        UserTransactionRequest invalidRequest = new UserTransactionRequest(senderEmail, receiverEmail, new BigDecimal("1000.00"), password, false);

        // Act & Assert
        AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
            userTransactionUseCase.execute(invalidRequest);
        });

        assertEquals("Insufficient balance", exception.getMessage());
        verify(userRepository).findUserByEmail(senderEmail);
        verify(userRepository).findUserByEmail(receiverEmail);
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }
}