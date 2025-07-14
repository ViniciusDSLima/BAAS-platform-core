package com.bank.baas.application.usecase;

import com.bank.baas.presentation.dto.UpdateAccountRequest;
import com.bank.baas.domain.exception.AuthorizationException;
import com.bank.baas.domain.model.Account;
import com.bank.baas.domain.model.User;
import com.bank.baas.domain.repository.AccountRepository;
import com.bank.baas.domain.repository.UserRepository;
import com.bank.baas.utils.log.SimpleLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

@Service
public class UpdateAccountUseCase {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public UpdateAccountUseCase(
            UserRepository userRepository,
            AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account execute(UpdateAccountRequest request) {
        SimpleLogger.info(UpdateAccountUseCase.class, "Starting account update process");

        User user = userRepository.findUserByEmail(request.email())
                .orElseThrow(() -> {
                    SimpleLogger.error(UpdateAccountUseCase.class, "User not found: " + request.email());
                    return new AuthorizationException("User not found: " + request.email());
                });

        Account account = user.getAccount();
        if (account == null) {
            SimpleLogger.error(UpdateAccountUseCase.class, "User has no account: " + user.getId());
            throw new AuthorizationException("User has no account");
        }

        if (!account.isPasswordCorrect(request.currentPassword())) {
            SimpleLogger.error(UpdateAccountUseCase.class, "Invalid password for account: " + account.getNumber());
            throw new AuthorizationException("Invalid password");
        }

        setAccountPassword(account, request.newPassword());
        account.setUpdatedAt(LocalDateTime.now());

        Account updatedAccount = accountRepository.save(account);
        SimpleLogger.info(UpdateAccountUseCase.class, "Updated account with ID: " + updatedAccount.getId());

        return updatedAccount;
    }

    private void setAccountPassword(Account account, String password) {
        try {
            Field passwordField = Account.class.getDeclaredField("password");
            passwordField.setAccessible(true);
            passwordField.set(account, password);
            SimpleLogger.info(UpdateAccountUseCase.class, "Password updated for account: " + account.getNumber());
        } catch (Exception e) {
            SimpleLogger.error(UpdateAccountUseCase.class, "Failed to update password: " + e.getMessage(), e);
            throw new RuntimeException("Failed to update password", e);
        }
    }
}
