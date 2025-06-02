package com.example.subscriptionapp.controller;

import com.example.subscriptionapp.dto.SubscriptionWithReminderRequest;
import com.example.subscriptionapp.model.Subscription;
import com.example.subscriptionapp.model.Reminder;
import com.example.subscriptionapp.model.ServiceProvider;
import com.example.subscriptionapp.model.User;
import com.example.subscriptionapp.service.SubscriptionService;
import com.example.subscriptionapp.service.ReminderService;
import com.example.subscriptionapp.service.ServiceProviderService;
import com.example.subscriptionapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@Tag(name = "Subscriptions", description = "API для управления подписками")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final ReminderService reminderService;
    private final ServiceProviderService serviceProviderService;
    private final UserService userService;

    public SubscriptionController(SubscriptionService subscriptionService, ReminderService reminderService, ServiceProviderService serviceProviderService, UserService userService) {
        this.subscriptionService = subscriptionService;
        this.reminderService = reminderService;
        this.serviceProviderService = serviceProviderService;
        this.userService = userService;
    }

    @Operation(summary = "Создать новую подписку (с напоминанием)")
    @PostMapping
    public ResponseEntity<Subscription> createSubscription(@RequestBody SubscriptionWithReminderRequest request, @RequestParam Long userId) {
        // Build Subscription entity from request
        Subscription subscription = new Subscription();
        subscription.setTitle(request.getTitle());
        subscription.setPrice(request.getPrice());
        subscription.setCurrency(request.getCurrency());
        subscription.setFirstPaymentDate(request.getFirstPaymentDate());
        subscription.setBillingPeriodValue(request.getBillingPeriodValue());
        subscription.setBillingPeriodUnit(request.getBillingPeriodUnit());
        subscription.setAutoRenew(request.getAutoRenew());
        subscription.setLogoOverrideUrl(request.getLogoOverrideUrl());
        subscription.setSubscriptionCategory(request.getSubscriptionCategory());
        subscription.setIsArchived(false);
        // Set user
        User user = userService.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        subscription.setUser(user);
        // Set service provider if present
        if (request.getServiceId() != null) {
            ServiceProvider provider = serviceProviderService.getServiceProviderById(request.getServiceId()).orElse(null);
            subscription.setService(provider);
        }
        // Save subscription
        Subscription created = subscriptionService.createSubscription(subscription);
        // If reminder is present, create it
        if (request.getReminder() != null) {
            SubscriptionWithReminderRequest.ReminderDto r = request.getReminder();
            Reminder reminder = new Reminder();
            reminder.setSubscription(created);
            reminder.setDaysBefore(r.getDaysBefore());
            reminder.setTimeOfDay(LocalTime.parse(r.getTimeOfDay()));
            reminder.setIsEnabled(r.getIsEnabled() != null ? r.getIsEnabled() : true);
            reminderService.createReminder(reminder);
        }
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Получить подписку по ID")
    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable Long id) {
        Subscription subscription = subscriptionService.getSubscriptionById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id " + id));
        return ResponseEntity.ok(subscription);
    }

    @Operation(summary = "Получить все активные подписки пользователя")
    @GetMapping("/active/user/{id}")
    public ResponseEntity<List<Subscription>> getAllActiveUserSubscriptions(@PathVariable Long id) {
        List<Subscription> subscriptions = subscriptionService.getAllActiveUserSubscriptions(id);
        return ResponseEntity.ok(subscriptions);
    }

    @Operation(summary = "Получить все заархивированные подписки пользователя")
    @GetMapping("/archived/user/{id}")
    public ResponseEntity<List<Subscription>> getAllArchivedUserSubscriptions(@PathVariable Long id) {
        List<Subscription> subscriptions = subscriptionService.getAllArchivedUserSubscriptions(id);
        return ResponseEntity.ok(subscriptions);
    }

    @Operation(summary = "Получить все подписки")
    @GetMapping
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @Operation(summary = "Архивировать подписку")
    @PutMapping("/archive/{id}")
    public ResponseEntity<Subscription> archiveSubscription(@PathVariable Long id) {
        Subscription updated = subscriptionService.archiveSubscription(id);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Восстановить подписку")
    @PutMapping("/restore/{id}")
    public ResponseEntity<Subscription> restoreSubscription(@PathVariable Long id) {
        Subscription updated = subscriptionService.restoreSubscription(id);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Обновить подписку")
    @PutMapping("/{id}")
    public ResponseEntity<Subscription> updateSubscription(@PathVariable Long id,
                                                           @RequestBody Subscription subscription) {
        Subscription updated = subscriptionService.updateSubscription(id, subscription);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить подписку")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
