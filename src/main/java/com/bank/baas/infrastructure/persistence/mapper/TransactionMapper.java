package com.bank.baas.infrastructure.persistence.mapper;

import com.bank.baas.domain.model.Transaction;
import com.bank.baas.presentation.dto.TransactionDTO;
import com.bank.baas.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TransactionMapper {

    private final UserMapper userMapper;

    @Autowired
    public TransactionMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public TransactionEntity toEntity(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        TransactionEntity entity = new TransactionEntity();
        entity.setId(transaction.getId());
        entity.setSender(userMapper.toEntity(transaction.getSender()));
        entity.setReceiver(userMapper.toEntity(transaction.getReceiver()));
        entity.setStatus(transaction.getStatus());
        entity.setAmount(transaction.getAmount());
        entity.setCreatedAt(transaction.getCreatedAt());

        if (transaction.getUpdatedAt() != null) {
            entity.setUpdatedAt(transaction.getUpdatedAt());
        }

        return entity;
    }

    public Transaction toDomain(TransactionEntity entity) {
        if (entity == null) {
            return null;
        }

        Transaction transaction = new Transaction(
            userMapper.toDomain(entity.getSender()),
            userMapper.toDomain(entity.getReceiver()),
            entity.getAmount(),
            entity.getCreatedAt()
        );

        setTransactionId(transaction, entity.getId());
        setTransactionStatus(transaction, entity.getStatus());

        if (entity.getUpdatedAt() != null) {
            setTransactionUpdatedAt(transaction, entity.getUpdatedAt());
        }

        return transaction;
    }

    private void setTransactionId(Transaction transaction, UUID id) {
        try {
            Field idField = Transaction.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(transaction, id);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível definir o ID da transação", e);
        }
    }

    private void setTransactionStatus(Transaction transaction, com.bank.baas.domain.enums.TransactionStatus status) {
        try {
            Field statusField = Transaction.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(transaction, status);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível definir o status da transação", e);
        }
    }

    private void setTransactionUpdatedAt(Transaction transaction, LocalDateTime updatedAt) {
        try {
            Field updatedAtField = Transaction.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(transaction, updatedAt);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível definir a data de atualização da transação", e);
        }
    }

    public TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return new TransactionDTO(
            transaction.getId(),
            transaction.getSender().getId(),
            transaction.getSender().getEmail(),
            transaction.getReceiver().getId(),
            transaction.getReceiver().getEmail(),
            transaction.getAmount(),
            transaction.getStatus(),
            transaction.getCreatedAt(),
            transaction.getUpdatedAt()
        );
    }
}
