package com.bank.baas.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class DepositCode {

    private UUID id;
    private String code;
    private BigDecimal amount;
    private User generator;
    private boolean used;
    private LocalDateTime createdAt;
    private LocalDateTime usedAt;
    private User usedBy;

    public DepositCode() {
    }

    public DepositCode(String code, BigDecimal amount, User generator) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.amount = amount;
        this.generator = generator;
        this.used = false;
        this.createdAt = LocalDateTime.now();
    }
    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }


    public BigDecimal getAmount() {
        return amount;
    }


    public User getGenerator() {
        return generator;
    }


    public boolean isUsed() {
        return used;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public LocalDateTime getUsedAt() {
        return usedAt;
    }


    public User getUsedBy() {
        return usedBy;
    }


    public void markAsUsed(User user) {
        if (this.used) {
            throw new IllegalStateException("Deposit code already used");
        }
        
        if (user.getId().equals(this.generator.getId())) {
            throw new IllegalStateException("Cannot use own deposit code");
        }
        
        this.used = true;
        this.usedAt = LocalDateTime.now();
        this.usedBy = user;
    }
}