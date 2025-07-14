package com.bank.baas.domain.repository;


import com.bank.baas.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findUserByCPF(String cpf);
    Optional<User> findUserByAccount(String account);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserById(UUID id);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    User save(User user);
}
