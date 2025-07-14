package com.bank.baas.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "deposit_codes")
public class DepositCodeEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "generator_id", nullable = false)
    private UserEntity generator;

    @Column(nullable = false)
    private boolean used;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @ManyToOne
    @JoinColumn(name = "used_by_id")
    private UserEntity usedBy;

    public DepositCodeEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public UserEntity getGenerator() {
        return generator;
    }

    public void setGenerator(UserEntity generator) {
        this.generator = generator;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public LocalDateTime getUsedAt() {
        return usedAt;
    }


    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }


    public UserEntity getUsedBy() {
        return usedBy;
    }


    public void setUsedBy(UserEntity usedBy) {
        this.usedBy = usedBy;
    }
}