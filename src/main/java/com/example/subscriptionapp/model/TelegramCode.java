package com.example.subscriptionapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelegramCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Связь с пользователем
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 6-значный код (уникальный)
    @Column(nullable = false, unique = true, length = 6)
    private String code;

    // Дата и время создания кода
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Дата и время истечения срока действия кода
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
