package com.example.subscriptionapp.repository;

import com.example.subscriptionapp.model.Reminder;
import com.example.subscriptionapp.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findBySubscription(Subscription subscription);
}
