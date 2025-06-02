package com.example.subscriptionapp.repository;

import com.example.subscriptionapp.model.HistoryLog;
import com.example.subscriptionapp.model.User;
import com.example.subscriptionapp.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryLogRepository extends JpaRepository<HistoryLog, Long> {
    // Получить все записи истории для конкретного пользователя
    List<HistoryLog> findByUser(User user);
    List<HistoryLog> findByUserId(Long id);
    List<HistoryLog> findBySubscription(Subscription subscription);
}
