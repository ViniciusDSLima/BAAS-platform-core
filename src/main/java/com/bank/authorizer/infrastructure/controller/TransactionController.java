package com.bank.authorizer.infrastructure.controller;

import com.bank.authorizer.domain.enums.AuthorizationTransactionStatus;
import com.bank.authorizer.domain.exception.AuthorizationException;
import com.bank.authorizer.domain.model.Transaction;
import com.bank.authorizer.application.usecase.CreateTransactionUseCase;
import com.bank.authorizer.infrastructure.dto.TransactionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacoes")
public class TransactionController {
    private final CreateTransactionUseCase createTransactionUseCase;

    public TransactionController(CreateTransactionUseCase createTransactionUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
    }

    @PostMapping
    public ResponseEntity<String> createTransaction(@RequestBody TransactionDTO transactionRequestDTO) {
        try {
            Transaction transaction = new Transaction(
                    transactionRequestDTO.numeroCartao(),
                    transactionRequestDTO.valor(),
                    transactionRequestDTO.senhaCartao()
            );

            AuthorizationTransactionStatus status = createTransactionUseCase.execute(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(status.name());

        } catch (AuthorizationException e){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }
}
