package com.bank.baas.presentation.dto;

import java.math.BigDecimal;

public record UserTransactionRequest(
    String senderIdentifier,
    String receiverIdentifier,
    BigDecimal amount,
    String password,
    boolean isCpf
) {
    @Override
    public String toString() {
        return "UserTransactionRequest{" +
                "senderIdentifier='" + senderIdentifier + '\'' +
                ", receiverIdentifier='" + receiverIdentifier + '\'' +
                ", amount=" + amount +
                ", password='[PROTECTED]'" +
                ", isCpf=" + isCpf +
                '}';
    }
}