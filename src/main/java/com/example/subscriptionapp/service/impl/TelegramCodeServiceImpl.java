package com.example.subscriptionapp.service.impl;

import com.example.subscriptionapp.model.TelegramCode;
import com.example.subscriptionapp.model.User;
import com.example.subscriptionapp.repository.TelegramCodeRepository;
import com.example.subscriptionapp.service.TelegramCodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class TelegramCodeServiceImpl implements TelegramCodeService {

    private final TelegramCodeRepository telegramCodeRepository;

    public TelegramCodeServiceImpl(TelegramCodeRepository telegramCodeRepository) {
        this.telegramCodeRepository = telegramCodeRepository;
    }

    @Override
    public Optional<TelegramCode> getTelegramCodeByCode(String code) {
        return telegramCodeRepository.findByCode(code);
    }

    @Override
    public TelegramCode generateAndSaveCode(User user) {
        String code;
        Random random = new Random();
        // Ensure uniqueness
        do {
            code = String.format("%06d", random.nextInt(1_000_000));
        } while (telegramCodeRepository.findByCode(code).isPresent());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);

        TelegramCode telegramCode = TelegramCode.builder()
                .user(user)
                .code(code)
                .createdAt(now)
                .expiresAt(expiresAt)
                .build();
        return telegramCodeRepository.save(telegramCode);
    }
}
