package com.bank.baas.infrastructure.persistence.repository.implementations;

import com.bank.baas.domain.model.Account;
import com.bank.baas.infrastructure.persistence.entity.AccountEntity;
import com.bank.baas.infrastructure.persistence.mapper.AccountMapper;
import com.bank.baas.infrastructure.persistence.repository.interfaces.SpringDataAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaAccountRepositoryTest {

    @Mock
    private SpringDataAccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private JpaAccountRepository jpaAccountRepository;

    private AccountEntity accountEntity;
    private Account account;
    private UUID accountId;
    private String accountNumber;
    private String agency;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        accountNumber = "123456";
        agency = "0001";
        
        accountEntity = new AccountEntity();
        accountEntity.setNumber(accountNumber);
        accountEntity.setAgency(agency);
        
        account = new Account(accountNumber, agency, "1234");
        
        // Set up reflection to set the ID
        try {
            java.lang.reflect.Field idField = AccountEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(accountEntity, accountId);
            
            idField = Account.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(account, accountId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set account ID", e);
        }
    }

    @Test
    void save_ShouldReturnSavedAccount() {
        // Arrange
        when(accountMapper.toEntity(account)).thenReturn(accountEntity);
        when(accountRepository.save(accountEntity)).thenReturn(accountEntity);
        when(accountMapper.toDomain(accountEntity)).thenReturn(account);

        // Act
        Account result = jpaAccountRepository.save(account);

        // Assert
        assertEquals(account, result);
        verify(accountMapper).toEntity(account);
        verify(accountRepository).save(accountEntity);
        verify(accountMapper).toDomain(accountEntity);
    }

    @Test
    void findById_ShouldReturnAccount_WhenAccountExists() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(accountEntity));
        when(accountMapper.toDomain(accountEntity)).thenReturn(account);

        // Act
        Optional<Account> result = jpaAccountRepository.findById(accountId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(account, result.get());
        verify(accountRepository).findById(accountId);
        verify(accountMapper).toDomain(accountEntity);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenAccountDoesNotExist() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act
        Optional<Account> result = jpaAccountRepository.findById(accountId);

        // Assert
        assertFalse(result.isPresent());
        verify(accountRepository).findById(accountId);
        verify(accountMapper, never()).toDomain(any());
    }

    @Test
    void findByNumber_ShouldReturnAccount_WhenAccountExists() {
        // Arrange
        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.of(accountEntity));
        when(accountMapper.toDomain(accountEntity)).thenReturn(account);

        // Act
        Optional<Account> result = jpaAccountRepository.findByNumber(accountNumber);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(account, result.get());
        verify(accountRepository).findByNumber(accountNumber);
        verify(accountMapper).toDomain(accountEntity);
    }

    @Test
    void findByNumber_ShouldReturnEmpty_WhenAccountDoesNotExist() {
        // Arrange
        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.empty());

        // Act
        Optional<Account> result = jpaAccountRepository.findByNumber(accountNumber);

        // Assert
        assertFalse(result.isPresent());
        verify(accountRepository).findByNumber(accountNumber);
        verify(accountMapper, never()).toDomain(any());
    }

    @Test
    void findByUserId_ShouldReturnAccount_WhenAccountExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(accountRepository.findByUserEntityId(userId)).thenReturn(Optional.of(accountEntity));
        when(accountMapper.toDomain(accountEntity)).thenReturn(account);

        // Act
        Optional<Account> result = jpaAccountRepository.findByUserId(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(account, result.get());
        verify(accountRepository).findByUserEntityId(userId);
        verify(accountMapper).toDomain(accountEntity);
    }

    @Test
    void findByUserId_ShouldReturnEmpty_WhenAccountDoesNotExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(accountRepository.findByUserEntityId(userId)).thenReturn(Optional.empty());

        // Act
        Optional<Account> result = jpaAccountRepository.findByUserId(userId);

        // Assert
        assertFalse(result.isPresent());
        verify(accountRepository).findByUserEntityId(userId);
        verify(accountMapper, never()).toDomain(any());
    }

    @Test
    void findByAgency_ShouldReturnAccounts_WhenAccountsExist() {
        // Arrange
        AccountEntity accountEntity2 = new AccountEntity();
        accountEntity2.setNumber("654321");
        accountEntity2.setAgency(agency);
        
        Account account2 = new Account("654321", agency, "1234");
        
        List<AccountEntity> accountEntities = Arrays.asList(accountEntity, accountEntity2);
        List<Account> accounts = Arrays.asList(account, account2);
        
        when(accountRepository.findByAgency(agency)).thenReturn(accountEntities);
        when(accountMapper.toDomain(accountEntity)).thenReturn(account);
        when(accountMapper.toDomain(accountEntity2)).thenReturn(account2);

        // Act
        List<Account> result = jpaAccountRepository.findByAgency(agency);

        // Assert
        assertEquals(2, result.size());
        assertEquals(accounts, result);
        verify(accountRepository).findByAgency(agency);
        verify(accountMapper, times(2)).toDomain(any(AccountEntity.class));
    }

    @Test
    void findByAgency_ShouldReturnEmptyList_WhenNoAccountsExist() {
        // Arrange
        when(accountRepository.findByAgency(agency)).thenReturn(List.of());

        // Act
        List<Account> result = jpaAccountRepository.findByAgency(agency);

        // Assert
        assertTrue(result.isEmpty());
        verify(accountRepository).findByAgency(agency);
        verify(accountMapper, never()).toDomain(any());
    }

    @Test
    void findAll_ShouldReturnAllAccounts() {
        // Arrange
        AccountEntity accountEntity2 = new AccountEntity();
        accountEntity2.setNumber("654321");
        accountEntity2.setAgency("0002");
        
        Account account2 = new Account("654321", "0002", "1234");
        
        List<AccountEntity> accountEntities = Arrays.asList(accountEntity, accountEntity2);
        List<Account> accounts = Arrays.asList(account, account2);
        
        when(accountRepository.findAll()).thenReturn(accountEntities);
        when(accountMapper.toDomain(accountEntity)).thenReturn(account);
        when(accountMapper.toDomain(accountEntity2)).thenReturn(account2);

        // Act
        List<Account> result = jpaAccountRepository.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals(accounts, result);
        verify(accountRepository).findAll();
        verify(accountMapper, times(2)).toDomain(any(AccountEntity.class));
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoAccountsExist() {
        // Arrange
        when(accountRepository.findAll()).thenReturn(List.of());

        // Act
        List<Account> result = jpaAccountRepository.findAll();

        // Assert
        assertTrue(result.isEmpty());
        verify(accountRepository).findAll();
        verify(accountMapper, never()).toDomain(any());
    }

    @Test
    void existsByNumber_ShouldReturnTrue_WhenAccountExists() {
        // Arrange
        when(accountRepository.existsByNumber(accountNumber)).thenReturn(true);

        // Act
        boolean result = jpaAccountRepository.existsByNumber(accountNumber);

        // Assert
        assertTrue(result);
        verify(accountRepository).existsByNumber(accountNumber);
    }

    @Test
    void existsByNumber_ShouldReturnFalse_WhenAccountDoesNotExist() {
        // Arrange
        when(accountRepository.existsByNumber(accountNumber)).thenReturn(false);

        // Act
        boolean result = jpaAccountRepository.existsByNumber(accountNumber);

        // Assert
        assertFalse(result);
        verify(accountRepository).existsByNumber(accountNumber);
    }
}