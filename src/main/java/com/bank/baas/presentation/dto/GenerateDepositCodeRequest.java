package com.bank.baas.presentation.dto;

import java.math.BigDecimal;

public record GenerateDepositCodeRequest(
    String email,
    String password,
    BigDecimal amount
) {
    @Override
    public String toString() {
        return "GenerateDepositCodeRequest{" +
                "email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", amount=" + amount +
                '}';
    }
}