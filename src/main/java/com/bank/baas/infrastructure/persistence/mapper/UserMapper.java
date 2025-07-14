package com.bank.baas.infrastructure.persistence.mapper;

import com.bank.baas.domain.model.User;
import com.bank.baas.infrastructure.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {
    private final AccountMapper accountMapper;

    @Autowired
    public UserMapper(@Lazy AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setCpf(user.getCpf());
        entity.setPassword(user.getPassword());
        entity.setRoles(user.getRoles());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());

        if (user.getAccount() != null) {
            entity.setAccount(accountMapper.toEntity(user.getAccount()));
        }

        return entity;
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        User user = new User(entity.getEmail(), entity.getCpf());
        user.setPassword(entity.getPassword());
        user.setRoles(entity.getRoles());
        setUserId(user, entity.getId());

        if (entity.getAccount() != null) {
            user.setAccount(accountMapper.toDomain(entity.getAccount()));
        }

        return user;
    }

    private void setUserId(User user, UUID id) {
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível definir o ID do usuário", e);
        }
    }
}