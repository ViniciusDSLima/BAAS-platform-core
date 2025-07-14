package com.bank.baas.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DepositCodeTest {

    @Test
    void testDefaultConstructor() {
        DepositCode depositCode = new DepositCode();
        
        assertNull(depositCode.getId(), "ID should be null");
        assertNull(depositCode.getCode(), "Code should be null");
        assertNull(depositCode.getAmount(), "Amount should be null");
        assertNull(depositCode.getGenerator(), "Generator should be null");
        assertFalse(depositCode.isUsed(), "Used should be false by default");
        assertNull(depositCode.getCreatedAt(), "CreatedAt should be null");
        assertNull(depositCode.getUsedAt(), "UsedAt should be null");
        assertNull(depositCode.getUsedBy(), "UsedBy should be null");
    }

    @Test
    void testConstructorWithCodeAmountAndGenerator() {
        String code = "DEP123";
        BigDecimal amount = new BigDecimal("100.00");
        User generator = new User("generator@example.com", "12345678900");
        
        DepositCode depositCode = new DepositCode(code, amount, generator);
        
        assertNotNull(depositCode.getId(), "ID should be generated");
        assertEquals(code, depositCode.getCode(), "Code should match");
        assertEquals(amount, depositCode.getAmount(), "Amount should match");
        assertEquals(generator, depositCode.getGenerator(), "Generator should match");
        assertFalse(depositCode.isUsed(), "Used should be false");
        assertNotNull(depositCode.getCreatedAt(), "CreatedAt should be initialized");
        assertNull(depositCode.getUsedAt(), "UsedAt should be null");
        assertNull(depositCode.getUsedBy(), "UsedBy should be null");
    }

    @Test
    void testMarkAsUsed() {
        String code = "DEP123";
        BigDecimal amount = new BigDecimal("100.00");
        User generator = createUserWithId("generator@example.com", "12345678900", UUID.randomUUID());
        User user = createUserWithId("user@example.com", "98765432100", UUID.randomUUID());
        
        DepositCode depositCode = new DepositCode(code, amount, generator);
        
        depositCode.markAsUsed(user);
        
        assertTrue(depositCode.isUsed(), "Used should be true");
        assertNotNull(depositCode.getUsedAt(), "UsedAt should be initialized");
        assertEquals(user, depositCode.getUsedBy(), "UsedBy should match");
    }

    @Test
    void testMarkAsUsedWhenAlreadyUsed() {
        String code = "DEP123";
        BigDecimal amount = new BigDecimal("100.00");
        User generator = createUserWithId("generator@example.com", "12345678900", UUID.randomUUID());
        User user1 = createUserWithId("user1@example.com", "98765432100", UUID.randomUUID());
        User user2 = createUserWithId("user2@example.com", "11122233344", UUID.randomUUID());
        
        DepositCode depositCode = new DepositCode(code, amount, generator);
        depositCode.markAsUsed(user1);
        
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            depositCode.markAsUsed(user2);
        }, "Should throw IllegalStateException when already used");
        
        assertEquals("Deposit code already used", exception.getMessage(), "Exception message should match");
        assertEquals(user1, depositCode.getUsedBy(), "UsedBy should remain unchanged");
    }

    @Test
    void testMarkAsUsedByGenerator() {
        String code = "DEP123";
        BigDecimal amount = new BigDecimal("100.00");
        User generator = createUserWithId("generator@example.com", "12345678900", UUID.randomUUID());
        
        DepositCode depositCode = new DepositCode(code, amount, generator);
        
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            depositCode.markAsUsed(generator);
        }, "Should throw IllegalStateException when used by generator");
        
        assertEquals("Cannot use own deposit code", exception.getMessage(), "Exception message should match");
        assertFalse(depositCode.isUsed(), "Used should remain false");
        assertNull(depositCode.getUsedAt(), "UsedAt should remain null");
        assertNull(depositCode.getUsedBy(), "UsedBy should remain null");
    }
    
    // Helper method to create a User with a specific ID
    private User createUserWithId(String email, String cpf, UUID id) {
        User user = new User(email, cpf);
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set user ID", e);
        }
        return user;
    }
}