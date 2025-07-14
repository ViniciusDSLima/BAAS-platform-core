package com.bank.baas.presentation.controller;

import com.bank.baas.presentation.dto.CreateAccountRequest;
import com.bank.baas.presentation.dto.UpdateAccountRequest;
import com.bank.baas.application.usecase.CreateAccountUseCase;
import com.bank.baas.application.usecase.UpdateAccountUseCase;
import com.bank.baas.domain.model.Account;
import com.bank.baas.utils.log.SimpleLogger;
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
@RequestMapping("/api/accounts")
@Tag(name = "Account", description = "Account management API")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final UpdateAccountUseCase updateAccountUseCase;

    @Autowired
    public AccountController(
            CreateAccountUseCase createAccountUseCase,
            UpdateAccountUseCase updateAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.updateAccountUseCase = updateAccountUseCase;
    }

    @PostMapping
    @Operation(summary = "Create a new account", description = "Creates a new bank account for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Account created successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = AccountResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        SimpleLogger.info(AccountController.class, "Received account creation request");

        try {
            Account account = createAccountUseCase.execute(request);
            AccountResponse response = new AccountResponse(
                    account.getId(),
                    account.getNumber(),
                    account.getAgency(),
                    account.getUser().getId(),
                    account.getUser().getEmail(),
                    account.getUser().getCpf(),
                    account.getBalance(),
                    account.getCreatedAt()
            );

            SimpleLogger.info(AccountController.class, 
                    "Account created successfully: " + account.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            SimpleLogger.error(AccountController.class, 
                    "Error creating account: " + e.getMessage(), e);

            throw e;
        }
    }

    @PutMapping
    @Operation(summary = "Update an account", description = "Updates an existing bank account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account updated successfully",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = AccountResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Account not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AccountResponse> updateAccount(@RequestBody UpdateAccountRequest request) {
        SimpleLogger.info(AccountController.class, "Received account update request");

        try {
            Account account = updateAccountUseCase.execute(request);
            AccountResponse response = new AccountResponse(
                    account.getId(),
                    account.getNumber(),
                    account.getAgency(),
                    account.getUser().getId(),
                    account.getUser().getEmail(),
                    account.getUser().getCpf(),
                    account.getBalance(),
                    account.getCreatedAt()
            );

            SimpleLogger.info(AccountController.class, 
                    "Account updated successfully: " + account.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SimpleLogger.error(AccountController.class, 
                    "Error updating account: " + e.getMessage(), e);

            throw e;
        }
    }

    public static class AccountResponse {
        private final java.util.UUID id;
        private final String number;
        private final String agency;
        private final java.util.UUID userId;
        private final String email;
        private final String cpf;
        private final java.math.BigDecimal balance;
        private final java.time.LocalDateTime createdAt;

        public AccountResponse(
                java.util.UUID id,
                String number,
                String agency,
                java.util.UUID userId,
                String email,
                String cpf,
                java.math.BigDecimal balance,
                java.time.LocalDateTime createdAt) {
            this.id = id;
            this.number = number;
            this.agency = agency;
            this.userId = userId;
            this.email = email;
            this.cpf = cpf;
            this.balance = balance;
            this.createdAt = createdAt;
        }

        public java.util.UUID getId() {
            return id;
        }

        public String getNumber() {
            return number;
        }

        public String getAgency() {
            return agency;
        }

        public java.util.UUID getUserId() {
            return userId;
        }

        public String getEmail() {
            return email;
        }

        public String getCpf() {
            return cpf;
        }

        public java.math.BigDecimal getBalance() {
            return balance;
        }

        public java.time.LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }
}
