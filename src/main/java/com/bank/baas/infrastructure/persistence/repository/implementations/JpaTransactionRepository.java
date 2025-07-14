package com.bank.baas.infrastructure.persistence.repository.implementations;

import com.bank.baas.domain.enums.TransactionStatus;
import com.bank.baas.domain.model.Transaction;
import com.bank.baas.domain.repository.TransactionRepository;
import com.bank.baas.infrastructure.persistence.mapper.TransactionMapper;
import com.bank.baas.infrastructure.persistence.mapper.UserMapper;
import com.bank.baas.infrastructure.persistence.repository.interfaces.SpringDataTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class JpaTransactionRepository implements TransactionRepository {

    private final SpringDataTransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final UserMapper userMapper;

    @Autowired
    public JpaTransactionRepository(
            SpringDataTransactionRepository transactionRepository,
            TransactionMapper transactionMapper,
            UserMapper userMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Transaction save(Transaction transaction) {
        var entity = transactionMapper.toEntity(transaction);
        var savedEntity = transactionRepository.save(entity);
        return transactionMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toDomain);
    }

    @Override
    public List<Transaction> findBySenderId(UUID senderId) {
        var senderEntity = userMapper.toEntity(null);
        senderEntity.setId(senderId);

        return transactionRepository.findBySender(senderEntity)
                .stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByReceiverId(UUID receiverId) {
        var receiverEntity = userMapper.toEntity(null);
        receiverEntity.setId(receiverId);

        return transactionRepository.findByReceiver(receiverEntity)
                .stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findBySenderIdOrReceiverId(UUID userId, UUID sameUserId) {
        var userEntity = userMapper.toEntity(null);
        userEntity.setId(userId);

        var sameUserEntity = userMapper.toEntity(null);
        sameUserEntity.setId(sameUserId);

        return transactionRepository.findBySenderOrReceiver(userEntity, sameUserEntity)
                .stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status)
                .stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }
}
