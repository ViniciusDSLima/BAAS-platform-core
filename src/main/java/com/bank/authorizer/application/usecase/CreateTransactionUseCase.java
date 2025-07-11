package com.bank.authorizer.application.usecase;

import com.bank.authorizer.domain.enums.AuthorizationTransactionStatus;
import com.bank.authorizer.domain.enums.RuleAuthorization;
import com.bank.authorizer.domain.exception.AuthorizationException;
import com.bank.authorizer.domain.model.Card;
import com.bank.authorizer.domain.model.Transaction;
import com.bank.authorizer.domain.repository.CardRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateTransactionUseCase {

    private final CardRepository cardRepository;

    public CreateTransactionUseCase(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Transactional
    public AuthorizationTransactionStatus execute(Transaction transaction) {
        Card card = validateCard(transaction);

        validatePassword(card, transaction);

        validateBalance(card, transaction);

        card.decreaseBalance(transaction.getValue());
        cardRepository.save(card);

        return AuthorizationTransactionStatus.OK;
    }

    private Card validateCard(Transaction transaction) {
        return cardRepository.findByCardNumber(transaction.getCardNumber())
                .orElseThrow(() -> new AuthorizationException(RuleAuthorization.CARD_NOT_FOUND.getMessage()));
    }

    private void validatePassword(Card card, Transaction transaction){
        if (!card.isPasswordCorrect(transaction.getPassword())) {
            throw new AuthorizationException(RuleAuthorization.WRONG_PASSWORD.getMessage());
        }
    }

    private void validateBalance(Card card, Transaction transaction){
        if (!card.hasSufficientBalance(transaction.getValue())) {
            throw new AuthorizationException(RuleAuthorization.INSUFFICIENT_BALANCE.getMessage());
        }
    }
}
