package com.example.subscriptionapp.service;

import com.example.subscriptionapp.model.HistoryActionType;
import com.example.subscriptionapp.model.HistoryLog;
import com.example.subscriptionapp.model.Subscription;
import com.example.subscriptionapp.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface HistoryLogService {
    HistoryLog createHistoryLog(HistoryLog historyLog);
    void logHistoryEvent(HistoryActionType actionType, User user, Subscription subscription, java.math.BigDecimal amountCharged, String currency);
    void logHistoryEvent(HistoryActionType actionType, User user, Subscription subscription, java.math.BigDecimal amountCharged, String currency, String subscriptionTitle);
    Optional<HistoryLog> getHistoryLogById(Long id);
    List<HistoryLog> getHistoryLogsByUser(User user);
    List<HistoryLog> getHistoryLogsByUserId(Long userId);
    List<HistoryLog> getAllHistoryLogs();
    void deleteHistoryLog(Long id);
}
