package com.bank.baas.domain.repository;


import com.bank.baas.domain.model.Account;
import com.bank.baas.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Account save(Account account);
    Optional<Account> findById(UUID id);
    Optional<Account> findByNumber(String number);
    Optional<Account> findByUserId(UUID userId);
    List<Account> findByAgency(String agency);
    List<Account> findAll();
    boolean existsByNumber(String number);
}

