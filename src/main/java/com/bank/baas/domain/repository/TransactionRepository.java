package com.bank.baas.domain.repository;


import com.bank.baas.domain.enums.TransactionStatus;
import com.bank.baas.domain.model.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(UUID id);
    List<Transaction> findBySenderId(UUID senderId);
    List<Transaction> findByReceiverId(UUID receiverId);
    List<Transaction> findBySenderIdOrReceiverId(UUID userId, UUID sameUserId);
    List<Transaction> findByStatus(TransactionStatus status);
    List<Transaction> findAll();
}

