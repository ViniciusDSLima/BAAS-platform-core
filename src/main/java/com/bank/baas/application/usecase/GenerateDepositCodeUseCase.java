package com.bank.baas.application.usecase;

import com.bank.baas.presentation.dto.GenerateDepositCodeRequest;
import com.bank.baas.domain.exception.AuthorizationException;
import com.bank.baas.domain.model.Account;
import com.bank.baas.domain.model.DepositCode;
import com.bank.baas.domain.model.User;
import com.bank.baas.domain.repository.DepositCodeRepository;
import com.bank.baas.domain.repository.UserRepository;
import com.bank.baas.utils.log.SimpleLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class GenerateDepositCodeUseCase {

    private final UserRepository userRepository;
    private final DepositCodeRepository depositCodeRepository;

    @Autowired
    public GenerateDepositCodeUseCase(
            UserRepository userRepository,
            DepositCodeRepository depositCodeRepository) {
        this.userRepository = userRepository;
        this.depositCodeRepository = depositCodeRepository;
    }

    @Transactional
    public DepositCode execute(GenerateDepositCodeRequest request) {
        SimpleLogger.info(GenerateDepositCodeUseCase.class, "Starting deposit code generation process");

        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            SimpleLogger.error(GenerateDepositCodeUseCase.class, "Invalid amount: " + request.amount());
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        User user = userRepository.findUserByEmail(request.email())
                .orElseThrow(() -> {
                    SimpleLogger.error(GenerateDepositCodeUseCase.class, "User not found: " + request.email());
                    return new AuthorizationException("User not found: " + request.email());
                });

        Account account = user.getAccount();
        if (account == null) {
            SimpleLogger.error(GenerateDepositCodeUseCase.class, "User has no account: " + user.getId());
            throw new AuthorizationException("User has no account");
        }

        if (!account.isPasswordCorrect(request.password())) {
            SimpleLogger.error(GenerateDepositCodeUseCase.class, "Invalid password for account: " + account.getNumber());
            throw new AuthorizationException("Invalid password");
        }

        String code = generateUniqueCode();
        SimpleLogger.info(GenerateDepositCodeUseCase.class, "Generated deposit code: " + code);

        DepositCode depositCode = new DepositCode(code, request.amount(), user);

        DepositCode savedDepositCode = depositCodeRepository.save(depositCode);
        SimpleLogger.info(GenerateDepositCodeUseCase.class, "Saved deposit code with ID: " + savedDepositCode.getId());

        return savedDepositCode;
    }

    private String generateUniqueCode() {
        Random random = new Random();
        String code;

        do {
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 8; i++) {
                int type = random.nextInt(3);
                if (type == 0) {
                    sb.append(random.nextInt(10));
                } else if (type == 1) {
                    sb.append((char) (random.nextInt(26) + 'A'));
                } else {
                    sb.append((char) (random.nextInt(26) + 'a'));
                }
            }
            code = sb.toString();
        } while (depositCodeRepository.existsByCode(code));

        return code;
    }
}
