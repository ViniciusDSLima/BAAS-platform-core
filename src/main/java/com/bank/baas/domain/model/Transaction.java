package com.bank.authorizer.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String cardNumber;

    private String password;

    @Column(nullable = false)
    private BigDecimal value;

    public Transaction() {
    }

    public Transaction(String cardNumber, BigDecimal value, String password) {
        this.id = UUID.randomUUID();
        this.cardNumber = cardNumber;
        this.value = value;
        this.password = password;
    }


    public UUID getId() {
        return id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public BigDecimal getValue() {
        return value;
    }
    public String getPassword() {
        return password;
    }
}
