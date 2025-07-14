package com.bank.baas.presentation.controller;

import com.bank.baas.presentation.dto.UserTransactionRequest;
import com.bank.baas.application.usecase.UserTransactionUseCase;
import com.bank.baas.domain.model.Transaction;
import com.bank.baas.presentation.dto.TransactionDTO;
import com.bank.baas.utils.log.SimpleLogger;
import com.bank.baas.infrastructure.persistence.mapper.TransactionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction", description = "Transaction management API")
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

    @PostMapping("/user-to-user")
    @Operation(summary = "Create user-to-user transaction", 
               description = "Creates a transaction between two users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transaction created successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = TransactionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "422", description = "Insufficient funds"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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
