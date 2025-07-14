package com.bank.baas.infrastructure.persistence.repository.interfaces;

import com.bank.baas.infrastructure.persistence.entity.DepositCodeEntity;
import com.bank.baas.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataDepositCodeRepository extends JpaRepository<DepositCodeEntity, UUID> {
    Optional<DepositCodeEntity> findByCode(String code);
    List<DepositCodeEntity> findByGenerator(UserEntity generator);
    List<DepositCodeEntity> findByGeneratorAndUsed(UserEntity generator, boolean used);
    List<DepositCodeEntity> findByUsedBy(UserEntity usedBy);
    boolean existsByCode(String code);
}