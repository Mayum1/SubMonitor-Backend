package com.example.subscriptionapp.controller;

import com.example.subscriptionapp.model.HistoryLog;
import com.example.subscriptionapp.model.User;
import com.example.subscriptionapp.service.HistoryLogService;
import com.example.subscriptionapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history-logs")
@Tag(name = "History Logs", description = "API для управления историей действий")
public class HistoryLogController {

    private final HistoryLogService historyLogService;
    private final UserService userService;

    public HistoryLogController(HistoryLogService historyLogService, UserService userService) {
        this.historyLogService = historyLogService;
        this.userService = userService;
    }

    @Operation(summary = "Создать запись в истории")
    @PostMapping
    public ResponseEntity<HistoryLog> createHistoryLog(@RequestBody HistoryLog historyLog) {
        HistoryLog created = historyLogService.createHistoryLog(historyLog);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Получить запись истории по ID")
    @GetMapping("/{id}")
    public ResponseEntity<HistoryLog> getHistoryLogById(@PathVariable Long id) {
        HistoryLog historyLog = historyLogService.getHistoryLogById(id)
                .orElseThrow(() -> new RuntimeException("History log not found with id " + id));
        return ResponseEntity.ok(historyLog);
    }

    @Operation(summary = "Получить все записи истории для пользователя")
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<HistoryLog>> getHistoryLogsByUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        List<HistoryLog> historyLogs = historyLogService.getHistoryLogsByUser(user);
        return ResponseEntity.ok(historyLogs);
    }

    @Operation(summary = "Получить все записи истории")
    @GetMapping
    public ResponseEntity<List<HistoryLog>> getAllHistoryLogs() {
        List<HistoryLog> historyLogs = historyLogService.getAllHistoryLogs();
        return ResponseEntity.ok(historyLogs);
    }

    @Operation(summary = "Удалить запись истории по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistoryLog(@PathVariable Long id) {
        historyLogService.deleteHistoryLog(id);
        return ResponseEntity.noContent().build();
    }
}
