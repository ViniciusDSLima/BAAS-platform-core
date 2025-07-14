package com.bank.baas.infrastructure.persistence.repository.interfaces;

import com.bank.baas.infrastructure.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataAccountRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByNumber(String number);
    Optional<AccountEntity> findByUserEntityId(UUID userId);
    List<AccountEntity> findByAgency(String agency);
    boolean existsByNumber(String number);
}
