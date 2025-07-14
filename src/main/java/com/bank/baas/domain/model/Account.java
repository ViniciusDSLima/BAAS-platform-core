package com.bank.baas.domain.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Account {
    private UUID id;

    private String number;

    private String agency;

    private BigDecimal balance;

    private String password;

    private User user;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Account() {
    }

    public Account(String number, String agency, String password) {
        this.id = UUID.randomUUID();
        this.number = number;
        this.password = password;
        this.agency = agency;
        this.balance = new BigDecimal("0");
        this.createdAt = LocalDateTime.now();
    }


    public Account(String number, String agency, User user, String password) {
        this.number = number;
        this.agency = agency;
        this.balance = new BigDecimal("0");
        this.user = user;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }

    public String getNumber() {
        return number;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public UUID getId() {
        return id;
    }

    public String getAgency() {
        return agency;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void decreaseBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero");
        }

        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Saldo insuficiente");
        }

        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();

    }

    public void increaseBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero");
        }

        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }


    public boolean isPasswordCorrect(String password) {
        return this.password.equals(password);
    }

    public boolean hasSufficientBalance(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }
}
