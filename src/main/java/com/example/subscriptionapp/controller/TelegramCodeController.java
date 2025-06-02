package com.example.subscriptionapp.controller;

import com.example.subscriptionapp.model.TelegramCode;
import com.example.subscriptionapp.model.User;
import com.example.subscriptionapp.service.TelegramCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/telegram-codes")
@Tag(name = "Telegram Codes", description = "API для управления 6-значными кодами привязки Telegram")
public class TelegramCodeController {

    private final TelegramCodeService telegramCodeService;

    public TelegramCodeController(TelegramCodeService telegramCodeService) {
        this.telegramCodeService = telegramCodeService;
    }

    @Operation(summary = "Получить код по значению")
    @GetMapping("/by-code/{code}")
    public ResponseEntity<TelegramCode> getTelegramCodeByCode(@PathVariable String code) {
        TelegramCode telegramCode = telegramCodeService.getTelegramCodeByCode(code)
                .orElseThrow(() -> new RuntimeException("Telegram code not found with code " + code));
        return ResponseEntity.ok(telegramCode);
    }
    
    @Operation(summary = "Сгенерировать новый код для текущего пользователя")
    @PostMapping("/generate")
    public ResponseEntity<TelegramCode> generateCode(@AuthenticationPrincipal User user) {
        TelegramCode telegramCode = telegramCodeService.generateAndSaveCode(user);
        return ResponseEntity.ok(telegramCode);
    }
}
