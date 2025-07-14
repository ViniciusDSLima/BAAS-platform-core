package com.bank.baas.presentation.dto;

import com.bank.baas.domain.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionDTO(
        UUID id,
        UUID senderId,
        String senderEmail,
        UUID receiverId,
        String receiverEmail,
        BigDecimal amount,
        TransactionStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
