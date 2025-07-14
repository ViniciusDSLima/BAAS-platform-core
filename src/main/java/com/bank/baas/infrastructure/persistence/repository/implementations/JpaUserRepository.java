package com.bank.baas.infrastructure.persistence.repository;

import com.bank.baas.domain.model.User;
import com.bank.baas.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserRepository implements UserRepository {

    private final StringDataUserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public JpaUserRepository(SpringDataUserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findUserByCPF(String cpf) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findUserByAccount(String account) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findUserById(UUID id) {
        return Optional.empty();
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return false;
    }
}
