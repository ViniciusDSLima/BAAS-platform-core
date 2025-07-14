package com.bank.baas.domain.repository;


import com.bank.baas.domain.model.Account;
import com.bank.baas.domain.model.User;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findUserByNumber(String number);
    Account save(Account account);
    void update(Account account);
}
