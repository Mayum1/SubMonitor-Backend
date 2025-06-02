package com.example.subscriptionapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "history_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Пользователь, с которым связано действие
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Подписка, с которой связано действие (может быть null)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "subscription_id", nullable = true)
    private Subscription subscription;

    // Тип действия
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private HistoryActionType actionType;

    // Сумма, если действие связано с автосписанием (например, списание средств)
    @Column(name = "amount_charged")
    private BigDecimal amountCharged;

    // Валюта, если применимо (например, "USD", "EUR")
    @Column(name = "currency", length = 10)
    private String currency;

    // Временная метка создания записи (автоматически заполняется)
    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;

    // Название подписки на момент действия (сохраняется даже если подписка удалена)
    @Column(name = "subscription_title", length = 255)
    private String subscriptionTitle;
}
