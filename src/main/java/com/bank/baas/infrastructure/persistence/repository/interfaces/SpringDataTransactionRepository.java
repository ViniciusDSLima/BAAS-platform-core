package com.bank.baas.infrastructure.persistence.repository.interfaces;

import com.bank.baas.domain.enums.TransactionStatus;
import com.bank.baas.infrastructure.persistence.entity.TransactionEntity;
import com.bank.baas.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataTransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findBySender(UserEntity sender);
    List<TransactionEntity> findByReceiver(UserEntity receiver);
    List<TransactionEntity> findBySenderOrReceiver(UserEntity sender, UserEntity receiver);
    List<TransactionEntity> findByStatus(TransactionStatus status);
}
