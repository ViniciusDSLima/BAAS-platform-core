package com.bank.baas.application.usecase;

import com.bank.baas.presentation.dto.UserTransactionRequest;
import com.bank.baas.domain.enums.TransactionStatus;
import com.bank.baas.domain.exception.AuthorizationException;
import com.bank.baas.domain.model.Account;
import com.bank.baas.domain.model.Transaction;
import com.bank.baas.domain.model.User;
import com.bank.baas.domain.repository.AccountRepository;
import com.bank.baas.domain.repository.TransactionRepository;
import com.bank.baas.domain.repository.UserRepository;
import com.bank.baas.utils.log.SimpleLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserTransactionUseCase {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public UserTransactionUseCase(
            UserRepository userRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction execute(UserTransactionRequest request) {
        SimpleLogger.info(UserTransactionUseCase.class, "Starting transaction between users");

        User sender = findUser(request.senderIdentifier(), request.isCpf());

        Account senderAccount = validateSenderAccount(sender, request.password());

        User receiver = findUser(request.receiverIdentifier(), request.isCpf());

        Account receiverAccount = validateReceiverAccount(receiver);

        validateAmount(request.amount(), senderAccount);

        Transaction transaction = new Transaction(sender, receiver, request.amount(), LocalDateTime.now());
        transaction.setId(java.util.UUID.randomUUID());

        try {
            senderAccount.decreaseBalance(request.amount());
            receiverAccount.increaseBalance(request.amount());

            accountRepository.save(senderAccount);
            accountRepository.save(receiverAccount);

            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setUpdatedAt(LocalDateTime.now());

            Transaction savedTransaction = transactionRepository.save(transaction);

            SimpleLogger.info(UserTransactionUseCase.class, 
                    "Transaction completed successfully: " + savedTransaction.getId());

            return savedTransaction;
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setUpdatedAt(LocalDateTime.now());

            transactionRepository.save(transaction);

            SimpleLogger.error(UserTransactionUseCase.class, 
                    "Transaction failed: " + e.getMessage(), e);

            throw new AuthorizationException("Transaction failed: " + e.getMessage());
        }
    }

    private User findUser(String identifier, boolean isCpf) {
        Optional<User> optionalUser;

        if (isCpf) {
            SimpleLogger.info(UserTransactionUseCase.class, "Finding user by CPF: " + identifier);
            optionalUser = userRepository.findUserByCPF(identifier);
        } else {
            SimpleLogger.info(UserTransactionUseCase.class, "Finding user by email: " + identifier);
            optionalUser = userRepository.findUserByEmail(identifier);
        }

        return optionalUser.orElseThrow(() -> {
            SimpleLogger.error(UserTransactionUseCase.class, "User not found: " + identifier);
            return new AuthorizationException("User not found: " + identifier);
        });
    }

    private Account validateSenderAccount(User sender, String password) {
        Account account = sender.getAccount();

        if (account == null) {
            SimpleLogger.error(UserTransactionUseCase.class, "Sender has no account: " + sender.getId());
            throw new AuthorizationException("Sender has no account");
        }

        if (!account.isPasswordCorrect(password)) {
            SimpleLogger.error(UserTransactionUseCase.class, "Invalid password for account: " + account.getNumber());
            throw new AuthorizationException("Invalid password");
        }

        return account;
    }

    private Account validateReceiverAccount(User receiver) {
        Account account = receiver.getAccount();

        if (account == null) {
            SimpleLogger.error(UserTransactionUseCase.class, "Receiver has no account: " + receiver.getId());
            throw new AuthorizationException("Receiver has no account");
        }

        return account;
    }

    private void validateAmount(java.math.BigDecimal amount, Account senderAccount) {
        if (amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            SimpleLogger.error(UserTransactionUseCase.class, "Invalid amount: " + amount);
            throw new AuthorizationException("Amount must be greater than zero");
        }

        if (!senderAccount.hasSufficientBalance(amount)) {
            SimpleLogger.error(UserTransactionUseCase.class, 
                    "Insufficient balance for account: " + senderAccount.getNumber() + 
                    ", amount: " + amount + 
                    ", balance: " + senderAccount.getBalance());
            throw new AuthorizationException("Insufficient balance");
        }
    }
}
