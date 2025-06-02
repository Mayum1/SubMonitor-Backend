package com.example.subscriptionapp.service;

import com.example.subscriptionapp.model.Reminder;
import com.example.subscriptionapp.model.Subscription;

import java.util.List;
import java.util.Optional;

public interface ReminderService {
    Reminder createReminder(Reminder reminder);
    Optional<Reminder> getReminderById(Long id);
    List<Reminder> getRemindersBySubscription(Subscription subscription);
    Reminder updateReminder(Long id, Reminder reminder);
    void deleteReminder(Long id);
}
