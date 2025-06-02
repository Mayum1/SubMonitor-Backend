package com.example.subscriptionapp.controller;

import com.example.subscriptionapp.model.TelegramLink;
import com.example.subscriptionapp.service.TelegramLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/telegram-links")
@Tag(name = "Telegram Links", description = "API для управления привязкой Telegram аккаунтов")
public class TelegramLinkController {

    private final TelegramLinkService telegramLinkService;

    public TelegramLinkController(TelegramLinkService telegramLinkService) {
        this.telegramLinkService = telegramLinkService;
    }

    @Operation(summary = "Привязать Telegram аккаунт к пользователю")
    @PostMapping
    public ResponseEntity<TelegramLink> createTelegramLink(@RequestBody TelegramLink telegramLink) {
        TelegramLink created = telegramLinkService.createTelegramLink(telegramLink);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Получить привязку Telegram аккаунта по userId")
    @GetMapping("/{userId}")
    public ResponseEntity<TelegramLink> getTelegramLink(@PathVariable Long userId) {
        TelegramLink telegramLink = telegramLinkService.getTelegramLinkByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Telegram link not found for user id " + userId));
        return ResponseEntity.ok(telegramLink);
    }

    @Operation(summary = "Обновить привязку Telegram аккаунта")
    @PutMapping("/{userId}")
    public ResponseEntity<TelegramLink> updateTelegramLink(@PathVariable Long userId,
                                                           @RequestBody TelegramLink telegramLink) {
        TelegramLink updated = telegramLinkService.updateTelegramLink(userId, telegramLink);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить привязку Telegram аккаунта")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteTelegramLink(@PathVariable Long userId) {
        telegramLinkService.deleteTelegramLink(userId);
        return ResponseEntity.noContent().build();
    }
}
