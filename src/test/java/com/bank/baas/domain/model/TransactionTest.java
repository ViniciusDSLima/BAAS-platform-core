package com.bank.baas.domain.model;

import com.bank.baas.domain.enums.TransactionStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void testDefaultConstructor() {
        Transaction transaction = new Transaction();
        
        assertNull(transaction.getId(), "ID should be null");
        assertNull(transaction.getSender(), "Sender should be null");
        assertNull(transaction.getReceiver(), "Receiver should be null");
        assertNull(transaction.getStatus(), "Status should be null");
        assertNull(transaction.getAmount(), "Amount should be null");
        assertNull(transaction.getCreatedAt(), "CreatedAt should be null");
        assertNull(transaction.getUpdatedAt(), "UpdatedAt should be null");
    }

    @Test
    void testConstructorWithSenderReceiverAmountAndCreatedAt() {
        User sender = new User("sender@example.com", "12345678900");
        User receiver = new User("receiver@example.com", "98765432100");
        BigDecimal amount = new BigDecimal("100.00");
        LocalDateTime createdAt = LocalDateTime.now();
        
        Transaction transaction = new Transaction(sender, receiver, amount, createdAt);
        
        assertEquals(sender, transaction.getSender(), "Sender should match");
        assertEquals(receiver, transaction.getReceiver(), "Receiver should match");
        assertEquals(TransactionStatus.PENDING, transaction.getStatus(), "Status should be PENDING");
        assertEquals(amount, transaction.getAmount(), "Amount should match");
        assertEquals(createdAt, transaction.getCreatedAt(), "CreatedAt should match");
        assertNull(transaction.getId(), "ID should be null");
        assertNull(transaction.getUpdatedAt(), "UpdatedAt should be null");
    }

    @Test
    void testSetId() {
        Transaction transaction = new Transaction();
        UUID id = UUID.randomUUID();
        
        transaction.setId(id);
        
        assertEquals(id, transaction.getId(), "ID should be updated");
    }

    @Test
    void testSetSender() {
        Transaction transaction = new Transaction();
        User sender = new User("sender@example.com", "12345678900");
        
        transaction.setSender(sender);
        
        assertEquals(sender, transaction.getSender(), "Sender should be updated");
    }

    @Test
    void testSetReceiver() {
        Transaction transaction = new Transaction();
        User receiver = new User("receiver@example.com", "98765432100");
        
        transaction.setReceiver(receiver);
        
        assertEquals(receiver, transaction.getReceiver(), "Receiver should be updated");
    }

    @Test
    void testSetStatus() {
        Transaction transaction = new Transaction();
        
        transaction.setStatus(TransactionStatus.SUCCESS);
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus(), "Status should be SUCCESS");
        
        transaction.setStatus(TransactionStatus.FAILED);
        assertEquals(TransactionStatus.FAILED, transaction.getStatus(), "Status should be FAILED");
        
        transaction.setStatus(TransactionStatus.PENDING);
        assertEquals(TransactionStatus.PENDING, transaction.getStatus(), "Status should be PENDING");
        
        transaction.setStatus(TransactionStatus.CANCELLED);
        assertEquals(TransactionStatus.CANCELLED, transaction.getStatus(), "Status should be CANCELLED");
    }

    @Test
    void testSetAmount() {
        Transaction transaction = new Transaction();
        BigDecimal amount = new BigDecimal("100.00");
        
        transaction.setAmount(amount);
        
        assertEquals(amount, transaction.getAmount(), "Amount should be updated");
    }

    @Test
    void testSetCreatedAt() {
        Transaction transaction = new Transaction();
        LocalDateTime createdAt = LocalDateTime.now();
        
        transaction.setCreatedAt(createdAt);
        
        assertEquals(createdAt, transaction.getCreatedAt(), "CreatedAt should be updated");
    }

    @Test
    void testSetUpdatedAt() {
        Transaction transaction = new Transaction();
        LocalDateTime updatedAt = LocalDateTime.now();
        
        transaction.setUpdatedAt(updatedAt);
        
        assertEquals(updatedAt, transaction.getUpdatedAt(), "UpdatedAt should be updated");
    }
}