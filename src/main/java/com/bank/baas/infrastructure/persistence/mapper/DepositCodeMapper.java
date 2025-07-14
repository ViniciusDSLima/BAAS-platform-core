package com.bank.baas.infrastructure.persistence.mapper;

import com.bank.baas.domain.model.DepositCode;
import com.bank.baas.infrastructure.persistence.entity.DepositCodeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DepositCodeMapper {

    private final UserMapper userMapper;

    @Autowired
    public DepositCodeMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public DepositCodeEntity toEntity(DepositCode depositCode) {
        if (depositCode == null) {
            return null;
        }

        DepositCodeEntity entity = new DepositCodeEntity();
        entity.setId(depositCode.getId());
        entity.setCode(depositCode.getCode());
        entity.setAmount(depositCode.getAmount());
        entity.setGenerator(userMapper.toEntity(depositCode.getGenerator()));
        entity.setUsed(depositCode.isUsed());
        entity.setCreatedAt(depositCode.getCreatedAt());
        
        if (depositCode.getUsedAt() != null) {
            entity.setUsedAt(depositCode.getUsedAt());
        }
        
        if (depositCode.getUsedBy() != null) {
            entity.setUsedBy(userMapper.toEntity(depositCode.getUsedBy()));
        }

        return entity;
    }

    public DepositCode toDomain(DepositCodeEntity entity) {
        if (entity == null) {
            return null;
        }

        DepositCode depositCode = new DepositCode(
                entity.getCode(),
                entity.getAmount(),
                userMapper.toDomain(entity.getGenerator())
        );
        
        try {
            java.lang.reflect.Field idField = DepositCode.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(depositCode, entity.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set deposit code ID", e);
        }
        
        if (entity.isUsed()) {
            try {
                java.lang.reflect.Field usedField = DepositCode.class.getDeclaredField("used");
                usedField.setAccessible(true);
                usedField.set(depositCode, true);
                
                if (entity.getUsedAt() != null) {
                    java.lang.reflect.Field usedAtField = DepositCode.class.getDeclaredField("usedAt");
                    usedAtField.setAccessible(true);
                    usedAtField.set(depositCode, entity.getUsedAt());
                }
                
                if (entity.getUsedBy() != null) {
                    java.lang.reflect.Field usedByField = DepositCode.class.getDeclaredField("usedBy");
                    usedByField.setAccessible(true);
                    usedByField.set(depositCode, userMapper.toDomain(entity.getUsedBy()));
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to set deposit code used properties", e);
            }
        }

        return depositCode;
    }
}