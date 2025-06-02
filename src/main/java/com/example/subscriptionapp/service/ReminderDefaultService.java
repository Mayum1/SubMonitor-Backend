package com.example.subscriptionapp.service;

import com.example.subscriptionapp.model.ReminderDefault;
import com.example.subscriptionapp.model.User;

import java.util.List;
import java.util.Optional;

public interface ReminderDefaultService {
    ReminderDefault createReminderDefault(ReminderDefault reminderDefault);
    Optional<ReminderDefault> getReminderDefaultById(Long id);
    List<ReminderDefault> getReminderDefaultsByUser(User user);
    ReminderDefault updateReminderDefault(Long id, ReminderDefault reminderDefault);
    void deleteReminderDefault(Long id);
}
