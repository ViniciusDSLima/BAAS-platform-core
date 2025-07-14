package com.bank.authorizer.domain.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.smartcardio.Card;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(
        name = "account",
        indexes = {
                @Index(name = "idx_account_number", columnList = "number", unique = true),
                @Index(name = "idx_account_user_id", columnList = "user_id", unique = true),
        }
)
public class Account {
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

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Account() {
    }

    public Account(String number, String password) {
        this.id = UUID.randomUUID();
        this.number = number;
        this.password = password;
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

    public Optional<LocalDateTime> getUpdatedAt() {
        return Optional.ofNullable(updatedAt);
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
        this.balance = this.balance.subtract(amount);
    }

    public boolean isPasswordCorrect(String password) {
        return this.password.equals(password);
    }

    public boolean hasSufficientBalance(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }
}
