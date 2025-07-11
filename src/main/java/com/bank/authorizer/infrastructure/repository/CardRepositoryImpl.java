package com.bank.authorizer.infrastructure.repository;

import com.bank.authorizer.domain.model.Card;
import com.bank.authorizer.domain.repository.CardRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CardRepositoryImpl implements CardRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Card save(Card cartao) {
        entityManager.merge(cartao);
        return cartao;
    }
    
    @Override
    public Optional<Card> findByCardNumber(String numeroCartao) {
        try {
            Card cartao = entityManager.find(Card.class, numeroCartao, LockModeType.PESSIMISTIC_WRITE);
            return Optional.ofNullable(cartao);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public boolean existsByCardNumber(String numeroCartao) {
        String jpql = "SELECT COUNT(c) FROM Card c WHERE c.number = :numeroCartao";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("numeroCartao", numeroCartao)
                .getSingleResult();
        return count > 0;
    }
}
