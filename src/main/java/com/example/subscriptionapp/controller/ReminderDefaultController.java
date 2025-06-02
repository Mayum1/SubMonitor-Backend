package com.example.subscriptionapp.controller;

import com.example.subscriptionapp.model.ReminderDefault;
import com.example.subscriptionapp.model.User;
import com.example.subscriptionapp.service.ReminderDefaultService;
import com.example.subscriptionapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminder-defaults")
@Tag(name = "Reminder Defaults", description = "API для управления настройками напоминаний по умолчанию")
public class ReminderDefaultController {

    private final ReminderDefaultService reminderDefaultService;
    private final UserService userService;

    public ReminderDefaultController(ReminderDefaultService reminderDefaultService, UserService userService) {
        this.reminderDefaultService = reminderDefaultService;
        this.userService = userService;
    }

    @Operation(summary = "Создать настройку напоминания по умолчанию для пользователя")
    @PostMapping
    public ResponseEntity<ReminderDefault> createReminderDefault(@RequestParam Long userId,
                                                                 @RequestBody ReminderDefault reminderDefault) {
        // Находим пользователя по userId
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        reminderDefault.setUser(user);
        ReminderDefault created = reminderDefaultService.createReminderDefault(reminderDefault);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Получить все настройки напоминаний по умолчанию для пользователя")
    @GetMapping
    public ResponseEntity<List<ReminderDefault>> getReminderDefaultsByUser(@RequestParam Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        List<ReminderDefault> defaults = reminderDefaultService.getReminderDefaultsByUser(user);
        return ResponseEntity.ok(defaults);
    }

    @Operation(summary = "Получить настройку напоминания по умолчанию по ID")
    @GetMapping("/{id}")
    public ResponseEntity<ReminderDefault> getReminderDefaultById(@PathVariable Long id) {
        ReminderDefault reminderDefault = reminderDefaultService.getReminderDefaultById(id)
                .orElseThrow(() -> new RuntimeException("Reminder default not found with id " + id));
        return ResponseEntity.ok(reminderDefault);
    }

    @Operation(summary = "Обновить настройку напоминания по умолчанию")
    @PutMapping("/{id}")
    public ResponseEntity<ReminderDefault> updateReminderDefault(@PathVariable Long id,
                                                                 @RequestBody ReminderDefault reminderDefault) {
        ReminderDefault updated = reminderDefaultService.updateReminderDefault(id, reminderDefault);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить настройку напоминания по умолчанию")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminderDefault(@PathVariable Long id) {
        reminderDefaultService.deleteReminderDefault(id);
        return ResponseEntity.noContent().build();
    }
}
