package com.bank.authorizer.application.usecase;

import com.bank.authorizer.domain.enums.RuleAuthorization;
import com.bank.authorizer.domain.exception.AuthorizationException;
import com.bank.authorizer.domain.model.Card;
import com.bank.authorizer.domain.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class GetCardBalanceUseCase {
    private final CardRepository cardRepository;

    public GetCardBalanceUseCase(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public BigDecimal execute(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
                .map(Card::getBalance)
                .orElseThrow(() -> new AuthorizationException(RuleAuthorization.CARD_NOT_FOUND.getMessage()));
    }
}
