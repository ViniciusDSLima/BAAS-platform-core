package com.bank.baas.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "account",
        indexes = {
                @Index(name = "idx_account_number", columnList = "number", unique = true),
                @Index(name = "idx_account_user_id", columnList = "user_id", unique = true),
        }
)
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String agency;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String password;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(
                    name = "fk_account_user",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
            )
    )
    private UserEntity userEntity;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public AccountEntity() {
    }

    public AccountEntity(String number, String password) {
        this.id = UUID.randomUUID();
        this.number = number;
        this.password = password;
        this.balance = new BigDecimal("0");
        this.createdAt = LocalDateTime.now();
    }


    public AccountEntity(String number, String agency, UserEntity userEntity, String password) {
        this.number = number;
        this.agency = agency;
        this.balance = new BigDecimal("0");
        this.userEntity = userEntity;
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

    public String getPassword() {
        return password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public UserEntity getUser() {
        return userEntity;
    }

    public void setUser(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void decreaseBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public boolean isPasswordCorrect(String password) {
        return this.password.equals(password);
    }

    public boolean hasSufficientBalance(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }
}
