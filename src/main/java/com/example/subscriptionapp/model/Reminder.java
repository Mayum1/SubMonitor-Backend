package com.example.subscriptionapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reminders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Напоминание привязано к конкретной подписке
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    @JsonIgnore
    private Subscription subscription;

    // За сколько дней до оплаты отправлять напоминание
    @Column(name = "days_before", nullable = false)
    private Integer daysBefore;

    // Время суток, когда нужно отправить уведомление
    @Column(name = "time_of_day", nullable = false)
    private LocalTime timeOfDay;

    // Флаг, включено ли данное напоминание
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    // Время последней отправки напоминания
    @Column(name = "last_sent_at")
    private LocalDateTime lastSentAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
