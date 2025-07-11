package com.bank.authorizer.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private BigDecimal balance;

    public Card() {
    }

    public Card( String number, String password) {
        this.id = UUID.randomUUID();
        this.number = number;
        this.password = password;
        this.balance = new BigDecimal("500.00");
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

    public String getPassword() {
        return password;
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
