package com.example.subscriptionapp.service;

import com.example.subscriptionapp.model.TelegramLink;
import java.util.Optional;

public interface TelegramLinkService {
    TelegramLink createTelegramLink(TelegramLink telegramLink);
    Optional<TelegramLink> getTelegramLinkByUserId(Long userId);
    TelegramLink updateTelegramLink(Long userId, TelegramLink telegramLink);
    void deleteTelegramLink(Long userId);
    void linkTelegramAccount(Long userId, String chatId);
}
