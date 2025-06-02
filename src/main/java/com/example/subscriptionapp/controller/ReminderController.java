package com.example.subscriptionapp.controller;

import com.example.subscriptionapp.model.Reminder;
import com.example.subscriptionapp.model.Subscription;
import com.example.subscriptionapp.service.ReminderService;
import com.example.subscriptionapp.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@Tag(name = "Reminders", description = "API для управления напоминаниями подписок")
public class ReminderController {

    private final ReminderService reminderService;
    private final SubscriptionService subscriptionService;

    public ReminderController(ReminderService reminderService, SubscriptionService subscriptionService) {
        this.reminderService = reminderService;
        this.subscriptionService = subscriptionService;
    }

    @Operation(summary = "Создать напоминание для подписки")
    @PostMapping
    public ResponseEntity<Reminder> createReminder(@RequestParam Long subscriptionId, @RequestBody Reminder reminder) {
        // Находим подписку по идентификатору
        Subscription subscription = subscriptionService.getSubscriptionById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id " + subscriptionId));
        reminder.setSubscription(subscription);
        Reminder created = reminderService.createReminder(reminder);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Получить напоминание по ID")
    @GetMapping("/{id}")
    public ResponseEntity<Reminder> getReminderById(@PathVariable Long id) {
        Reminder reminder = reminderService.getReminderById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found with id " + id));
        return ResponseEntity.ok(reminder);
    }

    @Operation(summary = "Получить все напоминания для подписки")
    @GetMapping
    public ResponseEntity<List<Reminder>> getRemindersBySubscription(@RequestParam Long subscriptionId) {
        Subscription subscription = subscriptionService.getSubscriptionById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id " + subscriptionId));
        List<Reminder> reminders = reminderService.getRemindersBySubscription(subscription);
        return ResponseEntity.ok(reminders);
    }

    @Operation(summary = "Обновить напоминание")
    @PutMapping("/{id}")
    public ResponseEntity<Reminder> updateReminder(@PathVariable Long id, @RequestBody Reminder reminder) {
        Reminder updated = reminderService.updateReminder(id, reminder);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить напоминание")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.noContent().build();
    }
}
