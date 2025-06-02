package com.example.subscriptionapp.service;

public interface TelegramNotificationService {
    void sendMessage(String chatId, String message);
} 