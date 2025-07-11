package com.bank.authorizer.infrastructure.controller;

import com.bank.authorizer.domain.exception.AlreadyExistsException;
import com.bank.authorizer.domain.exception.AuthorizationException;
import com.bank.authorizer.domain.model.Card;
import com.bank.authorizer.application.usecase.CreateCardUseCase;
import com.bank.authorizer.application.usecase.GetCardBalanceUseCase;
import com.bank.authorizer.infrastructure.dto.CardDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
public class CardController {
    private final CreateCardUseCase createCardUseCase;
    private final GetCardBalanceUseCase getCardBalanceUseCase;

    public CardController(CreateCardUseCase createCardUseCase, GetCardBalanceUseCase getCardBalanceUseCase) {
        this.createCardUseCase = createCardUseCase;
        this.getCardBalanceUseCase = getCardBalanceUseCase;
    }

    @PostMapping
    public ResponseEntity<CardDTO> createCard(@RequestBody CardDTO cardRequestDTO) {
        try{
            createCardUseCase.execute(cardRequestDTO.numeroCartao(), cardRequestDTO.senha());
            return ResponseEntity.status(HttpStatus.CREATED).body(cardRequestDTO);
        } catch (AlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(cardRequestDTO);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cardRequestDTO);
        }
    }

    @GetMapping("/{numeroCartao}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String numeroCartao) {
        try {
            BigDecimal balance = getCardBalanceUseCase.execute(numeroCartao);
            return ResponseEntity.ok(balance);
        } catch (AuthorizationException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
