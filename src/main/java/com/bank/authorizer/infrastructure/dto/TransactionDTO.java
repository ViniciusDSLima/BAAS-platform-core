package com.bank.authorizer.infrastructure.dto;

import java.math.BigDecimal;

public record TransactionDTO(String numeroCartao, String senhaCartao, BigDecimal valor) {
}
