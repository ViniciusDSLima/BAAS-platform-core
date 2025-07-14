package com.bank.baas.application.usecase;

import com.bank.baas.presentation.dto.CreateAccountRequest;
import com.bank.baas.domain.exception.AlreadyExistsException;
import com.bank.baas.domain.model.Account;
import com.bank.baas.domain.model.User;
import com.bank.baas.domain.repository.AccountRepository;
import com.bank.baas.domain.repository.UserRepository;
import com.bank.baas.utils.log.SimpleLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class CreateAccountUseCase {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public CreateAccountUseCase(
            UserRepository userRepository,
            AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account execute(CreateAccountRequest request) {
        SimpleLogger.info(CreateAccountUseCase.class, "Starting account creation process");

        validateRequest(request);

        User user = new User(request.email(), request.cpf());
        SimpleLogger.info(CreateAccountUseCase.class, "Created user with ID: " + user.getId());

        String accountNumber = generateAccountNumber();
        String agency = "0001";

        Account account = new Account(accountNumber, agency, user, request.password());
        SimpleLogger.info(CreateAccountUseCase.class, 
                "Created account with number: " + account.getNumber() + " for user: " + user.getId());

        user.setAccount(account);

        Account savedAccount = accountRepository.save(account);
        SimpleLogger.info(CreateAccountUseCase.class, "Saved account with ID: " + savedAccount.getId());

        return savedAccount;
    }

    private void validateRequest(CreateAccountRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            SimpleLogger.error(CreateAccountUseCase.class, "User with email already exists: " + request.email());
            throw new AlreadyExistsException("User with email already exists: " + request.email());
        }

        if (userRepository.existsByCpf(request.cpf())) {
            SimpleLogger.error(CreateAccountUseCase.class, "User with CPF already exists: " + request.cpf());
            throw new AlreadyExistsException("User with CPF already exists: " + request.cpf());
        }
    }

    private String generateAccountNumber() {
        Random random = new Random();
        String accountNumber;

        do {
            int number = 10000000 + random.nextInt(90000000);
            accountNumber = String.valueOf(number);
        } while (accountRepository.existsByNumber(accountNumber));

        return accountNumber;
    }
}
