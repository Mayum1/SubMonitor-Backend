package com.example.subscriptionapp.repository;

import com.example.subscriptionapp.model.TelegramLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelegramLinkRepository extends JpaRepository<TelegramLink, Long> {
    Optional<TelegramLink> findByUserId(Long userId);
    Optional<TelegramLink> findByChatId(String chatId);
}
