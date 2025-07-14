package com.bank.baas.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void testDefaultConstructor() {
        Account account = new Account();
        
        assertNull(account.getId(), "ID should be null");
        assertNull(account.getNumber(), "Number should be null");
        assertNull(account.getAgency(), "Agency should be null");
        assertNull(account.getBalance(), "Balance should be null");
        assertNull(account.getPassword(), "Password should be null");
        assertNull(account.getUser(), "User should be null");
        assertNull(account.getCreatedAt(), "CreatedAt should be null");
        assertNull(account.getUpdatedAt(), "UpdatedAt should be null");
    }

    @Test
    void testConstructorWithNumberAgencyAndPassword() {
        String number = "123456";
        String agency = "0001";
        String password = "1234";
        
        Account account = new Account(number, agency, password);
        
        assertNotNull(account.getId(), "ID should be generated");
        assertEquals(number, account.getNumber(), "Number should match");
        assertEquals(agency, account.getAgency(), "Agency should match");
        assertEquals(password, account.getPassword(), "Password should match");
        assertEquals(BigDecimal.ZERO, account.getBalance(), "Balance should be zero");
        assertNotNull(account.getCreatedAt(), "CreatedAt should be initialized");
        assertNull(account.getUser(), "User should be null");
        assertNull(account.getUpdatedAt(), "UpdatedAt should be null");
    }

    @Test
    void testConstructorWithNumberAgencyUserAndPassword() {
        String number = "123456";
        String agency = "0001";
        String password = "1234";
        User user = new User("test@example.com", "12345678900");
        
        Account account = new Account(number, agency, user, password);
        
        assertEquals(number, account.getNumber(), "Number should match");
        assertEquals(agency, account.getAgency(), "Agency should match");
        assertEquals(password, account.getPassword(), "Password should match");
        assertEquals(user, account.getUser(), "User should match");
        assertEquals(BigDecimal.ZERO, account.getBalance(), "Balance should be zero");
        assertNotNull(account.getCreatedAt(), "CreatedAt should be initialized");
        assertNull(account.getUpdatedAt(), "UpdatedAt should be null");
    }

    @Test
    void testSetUser() {
        Account account = new Account();
        User user = new User("test@example.com", "12345678900");
        
        account.setUser(user);
        
        assertEquals(user, account.getUser(), "User should be updated");
    }

    @Test
    void testSetUpdatedAt() {
        Account account = new Account();
        LocalDateTime updatedAt = LocalDateTime.now();
        
        account.setUpdatedAt(updatedAt);
        
        assertEquals(updatedAt, account.getUpdatedAt(), "UpdatedAt should be updated");
    }

    @Test
    void testDecreaseBalance() {
        Account account = new Account("123456", "0001", "1234");
        account.increaseBalance(new BigDecimal("100.00")); // Set initial balance
        BigDecimal amount = new BigDecimal("50.00");
        
        account.decreaseBalance(amount);
        
        assertEquals(new BigDecimal("50.00"), account.getBalance(), "Balance should be decreased");
        assertNotNull(account.getUpdatedAt(), "UpdatedAt should be updated");
    }

    @Test
    void testDecreaseBalanceWithInsufficientFunds() {
        Account account = new Account("123456", "0001", "1234");
        account.increaseBalance(new BigDecimal("40.00")); // Set initial balance
        BigDecimal amount = new BigDecimal("50.00");
        
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            account.decreaseBalance(amount);
        }, "Should throw IllegalStateException for insufficient funds");
        
        assertEquals("Saldo insuficiente", exception.getMessage(), "Exception message should match");
        assertEquals(new BigDecimal("40.00"), account.getBalance(), "Balance should remain unchanged");
    }

    @Test
    void testDecreaseBalanceWithNegativeAmount() {
        Account account = new Account("123456", "0001", "1234");
        account.increaseBalance(new BigDecimal("100.00")); // Set initial balance
        BigDecimal amount = new BigDecimal("-10.00");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            account.decreaseBalance(amount);
        }, "Should throw IllegalArgumentException for negative amount");
        
        assertEquals("O valor deve ser maior que zero", exception.getMessage(), "Exception message should match");
        assertEquals(new BigDecimal("100.00"), account.getBalance(), "Balance should remain unchanged");
    }

    @Test
    void testDecreaseBalanceWithZeroAmount() {
        Account account = new Account("123456", "0001", "1234");
        account.increaseBalance(new BigDecimal("100.00")); // Set initial balance
        BigDecimal amount = BigDecimal.ZERO;
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            account.decreaseBalance(amount);
        }, "Should throw IllegalArgumentException for zero amount");
        
        assertEquals("O valor deve ser maior que zero", exception.getMessage(), "Exception message should match");
        assertEquals(new BigDecimal("100.00"), account.getBalance(), "Balance should remain unchanged");
    }

    @Test
    void testIncreaseBalance() {
        Account account = new Account("123456", "0001", "1234");
        BigDecimal amount = new BigDecimal("50.00");
        
        account.increaseBalance(amount);
        
        assertEquals(new BigDecimal("50.00"), account.getBalance(), "Balance should be increased");
        assertNotNull(account.getUpdatedAt(), "UpdatedAt should be updated");
    }

    @Test
    void testIncreaseBalanceWithNegativeAmount() {
        Account account = new Account("123456", "0001", "1234");
        BigDecimal amount = new BigDecimal("-10.00");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            account.increaseBalance(amount);
        }, "Should throw IllegalArgumentException for negative amount");
        
        assertEquals("O valor deve ser maior que zero", exception.getMessage(), "Exception message should match");
        assertEquals(BigDecimal.ZERO, account.getBalance(), "Balance should remain unchanged");
    }

    @Test
    void testIncreaseBalanceWithZeroAmount() {
        Account account = new Account("123456", "0001", "1234");
        BigDecimal amount = BigDecimal.ZERO;
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            account.increaseBalance(amount);
        }, "Should throw IllegalArgumentException for zero amount");
        
        assertEquals("O valor deve ser maior que zero", exception.getMessage(), "Exception message should match");
        assertEquals(BigDecimal.ZERO, account.getBalance(), "Balance should remain unchanged");
    }

    @Test
    void testIsPasswordCorrect() {
        String password = "1234";
        Account account = new Account("123456", "0001", password);
        
        assertTrue(account.isPasswordCorrect(password), "Password should be correct");
        assertFalse(account.isPasswordCorrect("wrong"), "Password should be incorrect");
    }

    @Test
    void testHasSufficientBalance() {
        Account account = new Account("123456", "0001", "1234");
        account.increaseBalance(new BigDecimal("100.00")); // Set initial balance
        
        assertTrue(account.hasSufficientBalance(new BigDecimal("50.00")), "Should have sufficient balance for 50.00");
        assertTrue(account.hasSufficientBalance(new BigDecimal("100.00")), "Should have sufficient balance for 100.00");
        assertFalse(account.hasSufficientBalance(new BigDecimal("150.00")), "Should not have sufficient balance for 150.00");
    }
}