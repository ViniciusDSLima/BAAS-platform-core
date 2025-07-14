package com.bank.baas.infrastructure.dto;

import com.bank.baas.domain.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for transactions.
 * This class represents the data returned to clients for a transaction.
 */
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
