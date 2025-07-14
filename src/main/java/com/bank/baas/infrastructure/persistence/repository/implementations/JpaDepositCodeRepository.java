package com.bank.baas.infrastructure.persistence.repository.implementations;

import com.bank.baas.domain.model.DepositCode;
import com.bank.baas.domain.model.User;
import com.bank.baas.domain.repository.DepositCodeRepository;
import com.bank.baas.infrastructure.persistence.entity.DepositCodeEntity;
import com.bank.baas.infrastructure.persistence.entity.UserEntity;
import com.bank.baas.infrastructure.persistence.mapper.DepositCodeMapper;
import com.bank.baas.infrastructure.persistence.mapper.UserMapper;
import com.bank.baas.infrastructure.persistence.repository.interfaces.SpringDataDepositCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class JpaDepositCodeRepository implements DepositCodeRepository {

    private final SpringDataDepositCodeRepository depositCodeRepository;
    private final DepositCodeMapper depositCodeMapper;
    private final UserMapper userMapper;

    @Autowired
    public JpaDepositCodeRepository(
            SpringDataDepositCodeRepository depositCodeRepository,
            DepositCodeMapper depositCodeMapper,
            UserMapper userMapper) {
        this.depositCodeRepository = depositCodeRepository;
        this.depositCodeMapper = depositCodeMapper;
        this.userMapper = userMapper;
    }

    @Override
    public DepositCode save(DepositCode depositCode) {
        DepositCodeEntity entity = depositCodeMapper.toEntity(depositCode);
        DepositCodeEntity savedEntity = depositCodeRepository.save(entity);
        return depositCodeMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<DepositCode> findById(UUID id) {
        return depositCodeRepository.findById(id)
                .map(depositCodeMapper::toDomain);
    }

    @Override
    public Optional<DepositCode> findByCode(String code) {
        return depositCodeRepository.findByCode(code)
                .map(depositCodeMapper::toDomain);
    }

    @Override
    public List<DepositCode> findByGenerator(User generator) {
        UserEntity generatorEntity = userMapper.toEntity(generator);
        return depositCodeRepository.findByGenerator(generatorEntity).stream()
                .map(depositCodeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepositCode> findByGeneratorAndUsedFalse(User generator) {
        UserEntity generatorEntity = userMapper.toEntity(generator);
        return depositCodeRepository.findByGeneratorAndUsed(generatorEntity, false).stream()
                .map(depositCodeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepositCode> findByGeneratorAndUsedTrue(User generator) {
        UserEntity generatorEntity = userMapper.toEntity(generator);
        return depositCodeRepository.findByGeneratorAndUsed(generatorEntity, true).stream()
                .map(depositCodeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepositCode> findByUsedBy(User user) {
        UserEntity userEntity = userMapper.toEntity(user);
        return depositCodeRepository.findByUsedBy(userEntity).stream()
                .map(depositCodeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCode(String code) {
        return depositCodeRepository.existsByCode(code);
    }
}