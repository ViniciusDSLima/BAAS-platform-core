package com.bank.authorizer.application.usecase;

import com.bank.authorizer.domain.exception.AuthorizationException;
import com.bank.authorizer.domain.model.Card;
import com.bank.authorizer.domain.model.Transaction;
import com.bank.authorizer.domain.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CreateTransactionUseCaseTest {
    private CreateTransactionUseCase createTransactionUseCase;

    @Mock
    private CardRepository cardRepository;

    private Card card;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createTransactionUseCase = new CreateTransactionUseCase(cardRepository);

        card = new Card("1234567890123456", "1234");
        transaction = new Transaction("1234567890123456", new BigDecimal("100.00"), "1234");
    }

    @Test
    void shouldAuthorizeTransactionSuccessfully() {
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        createTransactionUseCase.execute(transaction);

        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void shouldFailWhenCardDoesNotExist() {
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(AuthorizationException.class, () -> {
            createTransactionUseCase.execute(transaction);
        });

        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void shouldFailWhenPasswordIsInvalid() {
        transaction = new Transaction("1234567890123456", new BigDecimal("100.00"), "wrong_password");
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(card));

        assertThrows(AuthorizationException.class, () -> {
            createTransactionUseCase.execute(transaction);
        });

        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void shouldFailWhenBalanceIsInsufficient() {
        transaction = new Transaction("1234567890123456", new BigDecimal("600.00"), "1234");
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(card));

        assertThrows(AuthorizationException.class, () -> {
            createTransactionUseCase.execute(transaction);
        });

        verify(cardRepository, never()).save(any(Card.class));
    }
}