package com.bank.baas.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {
    private UUID id;

    private String email;

    private String cpf;

    private String password;

    private Set<String> roles;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Account account;

    public User() {
        this.roles = new HashSet<>();
    }

    public User(String email, String cpf) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.cpf = cpf;
        this.createdAt = LocalDateTime.now();
        this.roles = new HashSet<>();
    }

    public User(String email, String cpf, String password) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.cpf = cpf;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.roles = new HashSet<>();
        this.roles.add("ROLE_USER");
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Account getAccount() {
        return account;
    }

    public String getCpf() {
        return cpf;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void addRole(String role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
    }

    public void setAccount(Account account) {
        this.account = account;

        if (account != null && account.getUser() != this) {
            account.setUser(this);
        }
    }

}
