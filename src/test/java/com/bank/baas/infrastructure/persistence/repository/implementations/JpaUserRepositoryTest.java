package com.bank.baas.infrastructure.persistence.repository.implementations;

import com.bank.baas.domain.model.User;
import com.bank.baas.infrastructure.persistence.entity.AccountEntity;
import com.bank.baas.infrastructure.persistence.entity.UserEntity;
import com.bank.baas.infrastructure.persistence.mapper.UserMapper;
import com.bank.baas.infrastructure.persistence.repository.interfaces.SpringDataAccountRepository;
import com.bank.baas.infrastructure.persistence.repository.interfaces.SpringDataUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaUserRepositoryTest {

    @Mock
    private SpringDataUserRepository userRepository;

    @Mock
    private SpringDataAccountRepository accountRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private JpaUserRepository jpaUserRepository;

    private UserEntity userEntity;
    private User user;
    private UUID userId;
    private String email;
    private String cpf;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        email = "test@example.com";
        cpf = "12345678900";
        
        userEntity = new UserEntity(email, cpf);
        user = new User(email, cpf);
        
        // Set up reflection to set the ID
        try {
            java.lang.reflect.Field idField = UserEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(userEntity, userId);
            
            idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, userId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set user ID", e);
        }
    }

    @Test
    void findUserByCPF_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findByCpf(cpf)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(user);

        // Act
        Optional<User> result = jpaUserRepository.findUserByCPF(cpf);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository).findByCpf(cpf);
        verify(userMapper).toDomain(userEntity);
    }

    @Test
    void findUserByCPF_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = jpaUserRepository.findUserByCPF(cpf);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findByCpf(cpf);
        verify(userMapper, never()).toDomain(any());
    }

    @Test
    void findUserByAccount_ShouldReturnUser_WhenAccountExists() {
        // Arrange
        String accountNumber = "123456";
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setNumber(accountNumber);
        accountEntity.setUser(userEntity);
        
        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.of(accountEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(user);

        // Act
        Optional<User> result = jpaUserRepository.findUserByAccount(accountNumber);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(accountRepository).findByNumber(accountNumber);
        verify(userMapper).toDomain(userEntity);
    }

    @Test
    void findUserByAccount_ShouldReturnEmpty_WhenAccountDoesNotExist() {
        // Arrange
        String accountNumber = "123456";
        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = jpaUserRepository.findUserByAccount(accountNumber);

        // Assert
        assertFalse(result.isPresent());
        verify(accountRepository).findByNumber(accountNumber);
        verify(userMapper, never()).toDomain(any());
    }

    @Test
    void findUserByEmail_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(user);

        // Act
        Optional<User> result = jpaUserRepository.findUserByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository).findByEmail(email);
        verify(userMapper).toDomain(userEntity);
    }

    @Test
    void findUserByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = jpaUserRepository.findUserByEmail(email);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findByEmail(email);
        verify(userMapper, never()).toDomain(any());
    }

    @Test
    void findUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(user);

        // Act
        Optional<User> result = jpaUserRepository.findUserById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository).findById(userId);
        verify(userMapper).toDomain(userEntity);
    }

    @Test
    void findUserById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = jpaUserRepository.findUserById(userId);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findById(userId);
        verify(userMapper, never()).toDomain(any());
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenUserExists() {
        // Arrange
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act
        boolean result = jpaUserRepository.existsByEmail(email);

        // Assert
        assertTrue(result);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // Act
        boolean result = jpaUserRepository.existsByEmail(email);

        // Assert
        assertFalse(result);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void existsByCpf_ShouldReturnTrue_WhenUserExists() {
        // Arrange
        when(userRepository.existsByCpf(cpf)).thenReturn(true);

        // Act
        boolean result = jpaUserRepository.existsByCpf(cpf);

        // Assert
        assertTrue(result);
        verify(userRepository).existsByCpf(cpf);
    }

    @Test
    void existsByCpf_ShouldReturnFalse_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.existsByCpf(cpf)).thenReturn(false);

        // Act
        boolean result = jpaUserRepository.existsByCpf(cpf);

        // Assert
        assertFalse(result);
        verify(userRepository).existsByCpf(cpf);
    }

    @Test
    void save_ShouldReturnSavedUser() {
        // Arrange
        when(userMapper.toEntity(user)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDomain(userEntity)).thenReturn(user);

        // Act
        User result = jpaUserRepository.save(user);

        // Assert
        assertEquals(user, result);
        verify(userMapper).toEntity(user);
        verify(userRepository).save(userEntity);
        verify(userMapper).toDomain(userEntity);
    }
}