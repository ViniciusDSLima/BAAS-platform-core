package com.bank.baas.domain.repository;


import com.bank.baas.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findUserByCPF(String cpf);
    Optional<User> findUserByAccount(String account);
    Optional<User> findUserByEmail(String email);
}
