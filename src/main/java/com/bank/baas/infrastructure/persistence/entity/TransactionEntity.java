package com.bank.baas.infrastructure.persistence.entity;

import com.bank.baas.domain.enums.TransactionStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "transactions",
        indexes = {
                @Index(name = "idx_transactions_sender_id", columnList = "sender_id"),
                @Index(name = "idx_transactions_receiver_id", columnList = "receiver_id")
        }
)
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(
            name = "sender_id",
            foreignKey = @ForeignKey(
                    name = "fk_transaction_sender",
                    foreignKeyDefinition = "FOREIGN KEY (sender_id) REFERENCES users(id)"
            )
    )
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(
            name = "receiver_id",
            foreignKey = @ForeignKey(
                    name = "fk_transaction_receiver",
                    foreignKeyDefinition = "FOREIGN KEY (receiver_id) REFERENCES users(id)"
            )
    )
    private UserEntity receiver;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public TransactionEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
