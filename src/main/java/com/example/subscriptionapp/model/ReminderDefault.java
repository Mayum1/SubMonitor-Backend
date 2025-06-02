package com.example.subscriptionapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "reminder_defaults")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderDefault {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Связь с пользователем
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // За сколько дней до окончания подписки отправлять напоминание
    @Column(name = "days_before", nullable = false)
    private Integer daysBefore;

    // Время суток для отправки уведомления
    @Column(name = "time_of_day", nullable = false)
    private LocalTime timeOfDay;

    // Флаг, включены ли напоминания по умолчанию
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = false;
}
