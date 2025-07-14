package com.bank.baas.domain.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testDefaultConstructor() {
        User user = new User();
        
        assertNotNull(user.getRoles(), "Roles should be initialized");
        assertTrue(user.getRoles().isEmpty(), "Roles should be empty");
        assertNull(user.getId(), "ID should be null");
        assertNull(user.getEmail(), "Email should be null");
        assertNull(user.getCpf(), "CPF should be null");
        assertNull(user.getPassword(), "Password should be null");
        assertNull(user.getCreatedAt(), "CreatedAt should be null");
        assertNull(user.getUpdatedAt(), "UpdatedAt should be null");
        assertNull(user.getAccount(), "Account should be null");
    }

    @Test
    void testConstructorWithEmailAndCpf() {
        String email = "test@example.com";
        String cpf = "12345678900";
        
        User user = new User(email, cpf);
        
        assertNotNull(user.getId(), "ID should be generated");
        assertEquals(email, user.getEmail(), "Email should match");
        assertEquals(cpf, user.getCpf(), "CPF should match");
        assertNotNull(user.getCreatedAt(), "CreatedAt should be initialized");
        assertNull(user.getPassword(), "Password should be null");
        assertNotNull(user.getRoles(), "Roles should be initialized");
        assertTrue(user.getRoles().isEmpty(), "Roles should be empty");
    }

    @Test
    void testConstructorWithEmailCpfAndPassword() {
        String email = "test@example.com";
        String cpf = "12345678900";
        String password = "password123";
        
        User user = new User(email, cpf, password);
        
        assertNotNull(user.getId(), "ID should be generated");
        assertEquals(email, user.getEmail(), "Email should match");
        assertEquals(cpf, user.getCpf(), "CPF should match");
        assertEquals(password, user.getPassword(), "Password should match");
        assertNotNull(user.getCreatedAt(), "CreatedAt should be initialized");
        assertNotNull(user.getRoles(), "Roles should be initialized");
        assertEquals(1, user.getRoles().size(), "Roles should contain one element");
        assertTrue(user.getRoles().contains("ROLE_USER"), "Roles should contain ROLE_USER");
    }

    @Test
    void testSetEmail() {
        User user = new User();
        String email = "test@example.com";
        
        user.setEmail(email);
        
        assertEquals(email, user.getEmail(), "Email should be updated");
    }

    @Test
    void testSetPassword() {
        User user = new User();
        String password = "password123";
        
        user.setPassword(password);
        
        assertEquals(password, user.getPassword(), "Password should be updated");
    }

    @Test
    void testSetRoles() {
        User user = new User();
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");
        
        user.setRoles(roles);
        
        assertEquals(roles, user.getRoles(), "Roles should be updated");
        assertTrue(user.getRoles().contains("ROLE_ADMIN"), "Roles should contain ROLE_ADMIN");
    }

    @Test
    void testAddRole() {
        User user = new User();
        
        user.addRole("ROLE_ADMIN");
        
        assertEquals(1, user.getRoles().size(), "Roles should contain one element");
        assertTrue(user.getRoles().contains("ROLE_ADMIN"), "Roles should contain ROLE_ADMIN");
        
        // Test adding a role when roles is null
        user.setRoles(null);
        user.addRole("ROLE_MANAGER");
        
        assertNotNull(user.getRoles(), "Roles should be initialized");
        assertEquals(1, user.getRoles().size(), "Roles should contain one element");
        assertTrue(user.getRoles().contains("ROLE_MANAGER"), "Roles should contain ROLE_MANAGER");
    }

    @Test
    void testSetAccount() {
        User user = new User("test@example.com", "12345678900");
        Account account = new Account();
        
        user.setAccount(account);
        
        assertEquals(account, user.getAccount(), "Account should be updated");
        assertEquals(user, account.getUser(), "Bidirectional relationship should be established");
        
        // Test setting account when account.getUser() returns this user
        Account account2 = new Account();
        account2.setUser(user);
        
        user.setAccount(account2);
        
        assertEquals(account2, user.getAccount(), "Account should be updated");
        assertEquals(user, account2.getUser(), "User reference should remain unchanged");
    }
}