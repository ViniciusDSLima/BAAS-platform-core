package com.bank.baas.infrastructure.controller;

import com.bank.baas.infrastructure.dto.UserTransactionRequest;
import com.bank.baas.application.usecase.UserTransactionUseCase;
import com.bank.baas.domain.model.Transaction;
import com.bank.baas.infrastructure.dto.TransactionDTO;
import com.bank.baas.infrastructure.log.SimpleLogger;
import com.bank.baas.infrastructure.persistence.mapper.TransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for transaction-related endpoints.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final UserTransactionUseCase userTransactionUseCase;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionController(
            UserTransactionUseCase userTransactionUseCase,
            TransactionMapper transactionMapper) {
        this.userTransactionUseCase = userTransactionUseCase;
        this.transactionMapper = transactionMapper;
    }

    /**
     * Endpoint for creating a transaction between users.
     *
     * @param request the transaction request
     * @return the created transaction
     */
    @PostMapping("/user-to-user")
    public ResponseEntity<TransactionDTO> createUserToUserTransaction(
            @RequestBody UserTransactionRequest request) {
        SimpleLogger.info(TransactionController.class, "Received user-to-user transaction request");

        try {
            Transaction transaction = userTransactionUseCase.execute(request);
            TransactionDTO transactionDTO = transactionMapper.toDTO(transaction);

            SimpleLogger.info(TransactionController.class, 
                    "User-to-user transaction created successfully: " + transaction.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(transactionDTO);
        } catch (Exception e) {
            SimpleLogger.error(TransactionController.class, 
                    "Error creating user-to-user transaction: " + e.getMessage(), e);

            throw e;
        }
    }
}
