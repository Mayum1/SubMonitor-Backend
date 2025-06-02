package com.example.subscriptionapp.service.impl;

import com.example.subscriptionapp.bot.SubscriptionAppBot;
import com.example.subscriptionapp.service.TelegramNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TelegramNotificationServiceImpl implements TelegramNotificationService {
    private final SubscriptionAppBot subscriptionAppBot;

    @Autowired
    public TelegramNotificationServiceImpl(SubscriptionAppBot subscriptionAppBot) {
        this.subscriptionAppBot = subscriptionAppBot;
    }

    @Override
    public void sendMessage(String chatId, String message) {
        subscriptionAppBot.sendMessage(chatId, message);
    }
} 