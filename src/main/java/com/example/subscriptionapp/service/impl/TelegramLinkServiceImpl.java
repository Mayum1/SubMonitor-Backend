package com.example.subscriptionapp.service.impl;

import com.example.subscriptionapp.model.TelegramLink;
import com.example.subscriptionapp.repository.TelegramLinkRepository;
import com.example.subscriptionapp.service.TelegramLinkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.subscriptionapp.repository.UserRepository;
import com.example.subscriptionapp.model.User;
import java.time.LocalDateTime;

import java.util.Optional;

@Service
@Transactional
public class TelegramLinkServiceImpl implements TelegramLinkService {

    private final TelegramLinkRepository telegramLinkRepository;
    private final UserRepository userRepository;

    public TelegramLinkServiceImpl(TelegramLinkRepository telegramLinkRepository, UserRepository userRepository) {
        this.telegramLinkRepository = telegramLinkRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TelegramLink createTelegramLink(TelegramLink telegramLink) {
        return telegramLinkRepository.save(telegramLink);
    }

    @Override
    public Optional<TelegramLink> getTelegramLinkByUserId(Long userId) {
        return telegramLinkRepository.findById(userId);
    }

    @Override
    public TelegramLink updateTelegramLink(Long userId, TelegramLink telegramLink) {
        TelegramLink existing = telegramLinkRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Telegram link not found for user id " + userId));
        existing.setChatId(telegramLink.getChatId());
        existing.setLinkedAt(telegramLink.getLinkedAt());
        return telegramLinkRepository.save(existing);
    }

    @Override
    public void deleteTelegramLink(Long userId) {
        telegramLinkRepository.deleteById(userId);
    }

    @Transactional
    public void linkTelegramAccount(Long userId, String chatId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        TelegramLink link = TelegramLink.builder()
            .user(user)
            .chatId(chatId)
            .linkedAt(LocalDateTime.now())
            .build();
        telegramLinkRepository.save(link);
    }
}
