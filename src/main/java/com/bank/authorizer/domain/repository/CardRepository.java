package com.bank.authorizer.domain.repository;

import com.bank.authorizer.domain.model.Card;

import java.util.Optional;

public interface CardRepository {
    Card save(Card card);
    Optional<Card> findByCardNumber(String cardNumber);
    boolean existsByCardNumber(String cardNumber);
}
