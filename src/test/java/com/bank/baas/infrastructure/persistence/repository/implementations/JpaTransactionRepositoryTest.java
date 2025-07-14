package com.bank.baas.infrastructure.persistence.repository.implementations;

import com.bank.baas.domain.enums.TransactionStatus;
import com.bank.baas.domain.model.Transaction;
import com.bank.baas.domain.model.User;
import com.bank.baas.infrastructure.persistence.entity.TransactionEntity;
import com.bank.baas.infrastructure.persistence.entity.UserEntity;
import com.bank.baas.infrastructure.persistence.mapper.TransactionMapper;
import com.bank.baas.infrastructure.persistence.mapper.UserMapper;
import com.bank.baas.infrastructure.persistence.repository.interfaces.SpringDataTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaTransactionRepositoryTest {

    @Mock
    private SpringDataTransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private JpaTransactionRepository jpaTransactionRepository;

    private TransactionEntity transactionEntity;
    private Transaction transaction;
    private UserEntity senderEntity;
    private UserEntity receiverEntity;
    private User sender;
    private User receiver;
    private UUID transactionId;
    private UUID senderId;
    private UUID receiverId;
    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
        senderId = UUID.randomUUID();
        receiverId = UUID.randomUUID();
        createdAt = LocalDateTime.now();
        
        sender = new User("sender@example.com", "12345678900");
        receiver = new User("receiver@example.com", "98765432100");
        
        senderEntity = new UserEntity("sender@example.com", "12345678900");
        receiverEntity = new UserEntity("receiver@example.com", "98765432100");
        
        transaction = new Transaction(sender, receiver, new BigDecimal("100.00"), createdAt);
        transactionEntity = new TransactionEntity();
        transactionEntity.setSender(senderEntity);
        transactionEntity.setReceiver(receiverEntity);
        transactionEntity.setStatus(TransactionStatus.PENDING);
        
        // Set up reflection to set the IDs
        try {
            java.lang.reflect.Field idField = TransactionEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(transactionEntity, transactionId);
            
            idField = Transaction.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(transaction, transactionId);
            
            idField = UserEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(senderEntity, senderId);
            
            idField = UserEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(receiverEntity, receiverId);
            
            idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(sender, senderId);
            
            idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(receiver, receiverId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set IDs", e);
        }
    }

    @Test
    void save_ShouldReturnSavedTransaction() {
        // Arrange
        when(transactionMapper.toEntity(transaction)).thenReturn(transactionEntity);
        when(transactionRepository.save(transactionEntity)).thenReturn(transactionEntity);
        when(transactionMapper.toDomain(transactionEntity)).thenReturn(transaction);

        // Act
        Transaction result = jpaTransactionRepository.save(transaction);

        // Assert
        assertEquals(transaction, result);
        verify(transactionMapper).toEntity(transaction);
        verify(transactionRepository).save(transactionEntity);
        verify(transactionMapper).toDomain(transactionEntity);
    }

    @Test
    void findById_ShouldReturnTransaction_WhenTransactionExists() {
        // Arrange
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transactionEntity));
        when(transactionMapper.toDomain(transactionEntity)).thenReturn(transaction);

        // Act
        Optional<Transaction> result = jpaTransactionRepository.findById(transactionId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(transaction, result.get());
        verify(transactionRepository).findById(transactionId);
        verify(transactionMapper).toDomain(transactionEntity);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenTransactionDoesNotExist() {
        // Arrange
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        // Act
        Optional<Transaction> result = jpaTransactionRepository.findById(transactionId);

        // Assert
        assertFalse(result.isPresent());
        verify(transactionRepository).findById(transactionId);
        verify(transactionMapper, never()).toDomain(any());
    }

    @Test
    void findBySenderId_ShouldReturnTransactions_WhenTransactionsExist() {
        // Arrange
        UserEntity mockSenderEntity = new UserEntity();
        mockSenderEntity.setId(senderId);
        
        TransactionEntity transactionEntity2 = new TransactionEntity();
        transactionEntity2.setSender(senderEntity);
        transactionEntity2.setReceiver(receiverEntity);
        
        Transaction transaction2 = new Transaction(sender, receiver, new BigDecimal("200.00"), createdAt);
        
        List<TransactionEntity> transactionEntities = Arrays.asList(transactionEntity, transactionEntity2);
        List<Transaction> transactions = Arrays.asList(transaction, transaction2);
        
        when(userMapper.toEntity(null)).thenReturn(mockSenderEntity);
        when(transactionRepository.findBySender(mockSenderEntity)).thenReturn(transactionEntities);
        when(transactionMapper.toDomain(transactionEntity)).thenReturn(transaction);
        when(transactionMapper.toDomain(transactionEntity2)).thenReturn(transaction2);

        // Act
        List<Transaction> result = jpaTransactionRepository.findBySenderId(senderId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(transactions, result);
        verify(userMapper).toEntity(null);
        verify(transactionRepository).findBySender(any(UserEntity.class));
        verify(transactionMapper, times(2)).toDomain(any(TransactionEntity.class));
    }

    @Test
    void findBySenderId_ShouldReturnEmptyList_WhenNoTransactionsExist() {
        // Arrange
        UserEntity mockSenderEntity = new UserEntity();
        mockSenderEntity.setId(senderId);
        
        when(userMapper.toEntity(null)).thenReturn(mockSenderEntity);
        when(transactionRepository.findBySender(mockSenderEntity)).thenReturn(List.of());

        // Act
        List<Transaction> result = jpaTransactionRepository.findBySenderId(senderId);

        // Assert
        assertTrue(result.isEmpty());
        verify(userMapper).toEntity(null);
        verify(transactionRepository).findBySender(any(UserEntity.class));
        verify(transactionMapper, never()).toDomain(any());
    }

    @Test
    void findByReceiverId_ShouldReturnTransactions_WhenTransactionsExist() {
        // Arrange
        UserEntity mockReceiverEntity = new UserEntity();
        mockReceiverEntity.setId(receiverId);
        
        TransactionEntity transactionEntity2 = new TransactionEntity();
        transactionEntity2.setSender(senderEntity);
        transactionEntity2.setReceiver(receiverEntity);
        
        Transaction transaction2 = new Transaction(sender, receiver, new BigDecimal("200.00"), createdAt);
        
        List<TransactionEntity> transactionEntities = Arrays.asList(transactionEntity, transactionEntity2);
        List<Transaction> transactions = Arrays.asList(transaction, transaction2);
        
        when(userMapper.toEntity(null)).thenReturn(mockReceiverEntity);
        when(transactionRepository.findByReceiver(mockReceiverEntity)).thenReturn(transactionEntities);
        when(transactionMapper.toDomain(transactionEntity)).thenReturn(transaction);
        when(transactionMapper.toDomain(transactionEntity2)).thenReturn(transaction2);

        // Act
        List<Transaction> result = jpaTransactionRepository.findByReceiverId(receiverId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(transactions, result);
        verify(userMapper).toEntity(null);
        verify(transactionRepository).findByReceiver(any(UserEntity.class));
        verify(transactionMapper, times(2)).toDomain(any(TransactionEntity.class));
    }

    @Test
    void findByReceiverId_ShouldReturnEmptyList_WhenNoTransactionsExist() {
        // Arrange
        UserEntity mockReceiverEntity = new UserEntity();
        mockReceiverEntity.setId(receiverId);
        
        when(userMapper.toEntity(null)).thenReturn(mockReceiverEntity);
        when(transactionRepository.findByReceiver(mockReceiverEntity)).thenReturn(List.of());

        // Act
        List<Transaction> result = jpaTransactionRepository.findByReceiverId(receiverId);

        // Assert
        assertTrue(result.isEmpty());
        verify(userMapper).toEntity(null);
        verify(transactionRepository).findByReceiver(any(UserEntity.class));
        verify(transactionMapper, never()).toDomain(any());
    }

    @Test
    void findBySenderIdOrReceiverId_ShouldReturnTransactions_WhenTransactionsExist() {
        // Arrange
        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setId(senderId);
        
        UserEntity mockSameUserEntity = new UserEntity();
        mockSameUserEntity.setId(senderId);
        
        TransactionEntity transactionEntity2 = new TransactionEntity();
        transactionEntity2.setSender(senderEntity);
        transactionEntity2.setReceiver(receiverEntity);
        
        Transaction transaction2 = new Transaction(sender, receiver, new BigDecimal("200.00"), createdAt);
        
        List<TransactionEntity> transactionEntities = Arrays.asList(transactionEntity, transactionEntity2);
        List<Transaction> transactions = Arrays.asList(transaction, transaction2);
        
        when(userMapper.toEntity(null)).thenReturn(mockUserEntity, mockSameUserEntity);
        when(transactionRepository.findBySenderOrReceiver(mockUserEntity, mockSameUserEntity)).thenReturn(transactionEntities);
        when(transactionMapper.toDomain(transactionEntity)).thenReturn(transaction);
        when(transactionMapper.toDomain(transactionEntity2)).thenReturn(transaction2);

        // Act
        List<Transaction> result = jpaTransactionRepository.findBySenderIdOrReceiverId(senderId, senderId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(transactions, result);
        verify(userMapper, times(2)).toEntity(null);
        verify(transactionRepository).findBySenderOrReceiver(any(UserEntity.class), any(UserEntity.class));
        verify(transactionMapper, times(2)).toDomain(any(TransactionEntity.class));
    }

    @Test
    void findBySenderIdOrReceiverId_ShouldReturnEmptyList_WhenNoTransactionsExist() {
        // Arrange
        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setId(senderId);
        
        UserEntity mockSameUserEntity = new UserEntity();
        mockSameUserEntity.setId(senderId);
        
        when(userMapper.toEntity(null)).thenReturn(mockUserEntity, mockSameUserEntity);
        when(transactionRepository.findBySenderOrReceiver(mockUserEntity, mockSameUserEntity)).thenReturn(List.of());

        // Act
        List<Transaction> result = jpaTransactionRepository.findBySenderIdOrReceiverId(senderId, senderId);

        // Assert
        assertTrue(result.isEmpty());
        verify(userMapper, times(2)).toEntity(null);
        verify(transactionRepository).findBySenderOrReceiver(any(UserEntity.class), any(UserEntity.class));
        verify(transactionMapper, never()).toDomain(any());
    }

    @Test
    void findByStatus_ShouldReturnTransactions_WhenTransactionsExist() {
        // Arrange
        TransactionEntity transactionEntity2 = new TransactionEntity();
        transactionEntity2.setSender(senderEntity);
        transactionEntity2.setReceiver(receiverEntity);
        transactionEntity2.setStatus(TransactionStatus.PENDING);
        
        Transaction transaction2 = new Transaction(sender, receiver, new BigDecimal("200.00"), createdAt);
        
        List<TransactionEntity> transactionEntities = Arrays.asList(transactionEntity, transactionEntity2);
        List<Transaction> transactions = Arrays.asList(transaction, transaction2);
        
        when(transactionRepository.findByStatus(TransactionStatus.PENDING)).thenReturn(transactionEntities);
        when(transactionMapper.toDomain(transactionEntity)).thenReturn(transaction);
        when(transactionMapper.toDomain(transactionEntity2)).thenReturn(transaction2);

        // Act
        List<Transaction> result = jpaTransactionRepository.findByStatus(TransactionStatus.PENDING);

        // Assert
        assertEquals(2, result.size());
        assertEquals(transactions, result);
        verify(transactionRepository).findByStatus(TransactionStatus.PENDING);
        verify(transactionMapper, times(2)).toDomain(any(TransactionEntity.class));
    }

    @Test
    void findByStatus_ShouldReturnEmptyList_WhenNoTransactionsExist() {
        // Arrange
        when(transactionRepository.findByStatus(TransactionStatus.PENDING)).thenReturn(List.of());

        // Act
        List<Transaction> result = jpaTransactionRepository.findByStatus(TransactionStatus.PENDING);

        // Assert
        assertTrue(result.isEmpty());
        verify(transactionRepository).findByStatus(TransactionStatus.PENDING);
        verify(transactionMapper, never()).toDomain(any());
    }

    @Test
    void findAll_ShouldReturnAllTransactions() {
        // Arrange
        TransactionEntity transactionEntity2 = new TransactionEntity();
        transactionEntity2.setSender(senderEntity);
        transactionEntity2.setReceiver(receiverEntity);
        
        Transaction transaction2 = new Transaction(sender, receiver, new BigDecimal("200.00"), createdAt);
        
        List<TransactionEntity> transactionEntities = Arrays.asList(transactionEntity, transactionEntity2);
        List<Transaction> transactions = Arrays.asList(transaction, transaction2);
        
        when(transactionRepository.findAll()).thenReturn(transactionEntities);
        when(transactionMapper.toDomain(transactionEntity)).thenReturn(transaction);
        when(transactionMapper.toDomain(transactionEntity2)).thenReturn(transaction2);

        // Act
        List<Transaction> result = jpaTransactionRepository.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals(transactions, result);
        verify(transactionRepository).findAll();
        verify(transactionMapper, times(2)).toDomain(any(TransactionEntity.class));
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoTransactionsExist() {
        // Arrange
        when(transactionRepository.findAll()).thenReturn(List.of());

        // Act
        List<Transaction> result = jpaTransactionRepository.findAll();

        // Assert
        assertTrue(result.isEmpty());
        verify(transactionRepository).findAll();
        verify(transactionMapper, never()).toDomain(any());
    }
}