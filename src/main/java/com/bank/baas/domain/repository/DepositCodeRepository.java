package com.bank.baas.domain.repository;

import com.bank.baas.domain.model.DepositCode;
import com.bank.baas.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepositCodeRepository {
    DepositCode save(DepositCode depositCode);
    Optional<DepositCode> findById(UUID id);
    Optional<DepositCode> findByCode(String code);
    List<DepositCode> findByGenerator(User generator);
    List<DepositCode> findByGeneratorAndUsedFalse(User generator);
    List<DepositCode> findByGeneratorAndUsedTrue(User generator);
    List<DepositCode> findByUsedBy(User user);
    boolean existsByCode(String code);
}