package com.bank.authorizer.application.usecase;

import com.bank.authorizer.domain.exception.AlreadyExistsException;
import com.bank.authorizer.domain.repository.CardRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.bank.authorizer.domain.model.Card;

@Service
public class CreateCardUseCase {
    private final CardRepository cardRepository;

    public CreateCardUseCase(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Transactional
    public Card execute(String cardNumber, String password) {
        if (cardRepository.existsByCardNumber(cardNumber)){
            throw new AlreadyExistsException("Card already exists");
        }

        Card newCard = new Card(cardNumber, password);

        return cardRepository.save(newCard);
    }
}
