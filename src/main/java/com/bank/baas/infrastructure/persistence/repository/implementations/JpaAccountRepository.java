package com.bank.baas.infrastructure.persistence.repository.implementations;

import com.bank.baas.domain.model.Account;
import com.bank.baas.domain.repository.AccountRepository;
import com.bank.baas.infrastructure.persistence.entity.AccountEntity;
import com.bank.baas.infrastructure.persistence.mapper.AccountMapper;
import com.bank.baas.infrastructure.persistence.repository.interfaces.SpringDataAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class JpaAccountRepository implements AccountRepository {

    private final SpringDataAccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Autowired
    public JpaAccountRepository(SpringDataAccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public Account save(Account account) {
        AccountEntity accountEntity = accountMapper.toEntity(account);
        AccountEntity savedEntity = accountRepository.save(accountEntity);
        return accountMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return accountRepository.findById(id)
                .map(accountMapper::toDomain);
    }

    @Override
    public Optional<Account> findByNumber(String number) {
        return accountRepository.findByNumber(number)
                .map(accountMapper::toDomain);
    }

    @Override
    public Optional<Account> findByUserId(UUID userId) {
        return accountRepository.findByUserEntityId(userId)
                .map(accountMapper::toDomain);
    }

    @Override
    public List<Account> findByAgency(String agency) {
        return accountRepository.findByAgency(agency)
                .stream()
                .map(accountMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll()
                .stream()
                .map(accountMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNumber(String number) {
        return accountRepository.existsByNumber(number);
    }
}
