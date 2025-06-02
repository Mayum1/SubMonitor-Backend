package com.example.subscriptionapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Пользователь, которому принадлежит подписка
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Провайдер подписки
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ServiceProvider service;

    // Название подписки
    @Column(nullable = false, length = 255)
    private String title;

    // Цена подписки
    @Column(nullable = false)
    private BigDecimal price;

    // Валюта, например, "RUB", "USD", "EUR" и т.д.
    @Column(nullable = false, length = 10)
    private String currency;

    // Дата первого платежа
    @Column(name = "first_payment_date", nullable = false)
    private LocalDate firstPaymentDate;

    // Дата следующего платежа
    @Column(name = "next_payment_date")
    private LocalDate nextPaymentDate;

    // Значение периода оплаты (например, 1, 2, 3 и т.д.)
    @Column(name = "billing_period_value", nullable = false)
    private Integer billingPeriodValue;

    // Единица периода оплаты (DAY, MONTH, YEAR)
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_period_unit", nullable = false, length = 10)
    private BillingPeriodUnit billingPeriodUnit;

    // Флаг автопродления подписки
    @Column(name = "auto_renew", nullable = false)
    private Boolean autoRenew = true;

    // Архивирована ли подписка
    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived = false;

    // URL логотипа подписки (если пользователь переопределяет стандартный логотип)
    @Column(name = "logo_override_url")
    private String logoOverrideUrl;

    // Категория подписки
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_category", nullable = false, length = 50)
    private SubscriptionCategory subscriptionCategory;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reminder> reminders;
}
