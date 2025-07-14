package com.bank.baas.infrastructure.persistence.repository.implementations;

import com.bank.baas.domain.model.User;
import com.bank.baas.domain.repository.UserRepository;
import com.bank.baas.infrastructure.persistence.entity.UserEntity;
import com.bank.baas.infrastructure.persistence.mapper.UserMapper;
import com.bank.baas.infrastructure.persistence.repository.interfaces.SpringDataAccountRepository;
import com.bank.baas.infrastructure.persistence.repository.interfaces.SpringDataUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository userRepository;
    private final SpringDataAccountRepository accountRepository;
    private final UserMapper userMapper;

    @Autowired
    public JpaUserRepository(SpringDataUserRepository userRepository, SpringDataAccountRepository accountRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findUserByCPF(String cpf) {
        return userRepository.findByCpf(cpf)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findUserByAccount(String account) {
        return accountRepository.findByNumber(account)
                .map(accountEntity -> accountEntity.getUser())
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findUserById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return userRepository.existsByCpf(cpf);
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userMapper.toEntity(user);
        UserEntity savedEntity = userRepository.save(userEntity);
        return userMapper.toDomain(savedEntity);
    }
}