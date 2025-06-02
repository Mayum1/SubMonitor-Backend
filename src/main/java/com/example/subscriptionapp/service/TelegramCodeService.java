package com.example.subscriptionapp.service;

import com.example.subscriptionapp.model.TelegramCode;
import com.example.subscriptionapp.model.User;

import java.util.Optional;

public interface TelegramCodeService {
    Optional<TelegramCode> getTelegramCodeByCode(String code);
    TelegramCode generateAndSaveCode(User user);
}
