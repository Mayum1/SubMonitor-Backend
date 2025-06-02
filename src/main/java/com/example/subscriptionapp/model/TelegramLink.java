package com.example.subscriptionapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_links")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelegramLink {

    @Id
    private Long id;

    // Связь «один к одному» с пользователем, при этом PK совпадает с user.id
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    // Telegram chat id (идентификатор чата в Telegram)
    @Column(name = "telegram_chat_id", nullable = false)
    private String chatId;

    // Дата и время привязки аккаунта
    @Column(name = "linked_at", nullable = false)
    private LocalDateTime linkedAt;
}
