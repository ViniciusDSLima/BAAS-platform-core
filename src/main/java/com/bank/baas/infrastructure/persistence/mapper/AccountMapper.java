package com.bank.baas.infrastructure.persistence.mapper;

import com.bank.baas.domain.model.Account;
import com.bank.baas.infrastructure.persistence.entity.AccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.UUID;

@Component
public class AccountMapper {

    private final UserMapper userMapper;
    private boolean isCircularMappingInProgress = false;

    @Autowired
    public AccountMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public AccountEntity toEntity(Account account) {
        if (account == null) {
            return null;
        }

        if (isCircularMappingInProgress) {
            AccountEntity entity = new AccountEntity();
            entity.setId(account.getId());
            return entity;
        }

        isCircularMappingInProgress = true;

        try {
            AccountEntity entity = new AccountEntity();
            entity.setId(account.getId());
            entity.setNumber(account.getNumber());
            entity.setAgency(account.getAgency());
            entity.setBalance(account.getBalance());
            entity.setPassword(account.getPassword());
            entity.setCreatedAt(account.getCreatedAt());

            if (account.getUpdatedAt() != null) {
                entity.setUpdatedAt(account.getUpdatedAt());
            }

            if (account.getUser() != null) {
                entity.setUser(userMapper.toEntity(account.getUser()));
            }

            return entity;
        } finally {
            isCircularMappingInProgress = false;
        }
    }


    public Account toDomain(AccountEntity entity) {
        if (entity == null) {
            return null;
        }

        if (isCircularMappingInProgress) {
            Account account = new Account(entity.getNumber(), entity.getAgency(), entity.getPassword());
            setAccountId(account, entity.getId());
            return account;
        }

        isCircularMappingInProgress = true;

        try {
            Account account = new Account(entity.getNumber(), entity.getAgency(), entity.getPassword());
            setAccountId(account, entity.getId());
            setAccountBalance(account, entity.getBalance());
            setAccountCreatedAt(account, entity.getCreatedAt());

            if (entity.getUpdatedAt() != null) {
                setAccountUpdatedAt(account, entity.getUpdatedAt());
            }

            if (entity.getUser() != null) {
                account.setUser(userMapper.toDomain(entity.getUser()));
            }

            return account;
        } finally {
            isCircularMappingInProgress = false;
        }
    }

    private void setAccountId(Account account, UUID id) {
        try {
            Field idField = Account.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(account, id);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível definir o ID da conta", e);
        }
    }

    private void setAccountBalance(Account account, java.math.BigDecimal balance) {
        try {
            Field balanceField = Account.class.getDeclaredField("balance");
            balanceField.setAccessible(true);
            balanceField.set(account, balance);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível definir o saldo da conta", e);
        }
    }

    private void setAccountCreatedAt(Account account, java.time.LocalDateTime createdAt) {
        try {
            Field createdAtField = Account.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(account, createdAt);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível definir a data de criação da conta", e);
        }
    }

    private void setAccountUpdatedAt(Account account, java.time.LocalDateTime updatedAt) {
        try {
            Field updatedAtField = Account.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(account, updatedAt);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível definir a data de atualização da conta", e);
        }
    }

}
