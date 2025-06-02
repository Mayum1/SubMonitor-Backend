package com.example.subscriptionapp.service.impl;

import com.example.subscriptionapp.model.HistoryActionType;
import com.example.subscriptionapp.model.HistoryLog;
import com.example.subscriptionapp.model.Subscription;
import com.example.subscriptionapp.model.User;
import com.example.subscriptionapp.repository.HistoryLogRepository;
import com.example.subscriptionapp.service.HistoryLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HistoryLogServiceImpl implements HistoryLogService {

    private final HistoryLogRepository historyLogRepository;

    public HistoryLogServiceImpl(HistoryLogRepository historyLogRepository) {
        this.historyLogRepository = historyLogRepository;
    }

    @Override
    public HistoryLog createHistoryLog(HistoryLog historyLog) {
        return historyLogRepository.save(historyLog);
    }

    @Override
    public void logHistoryEvent(HistoryActionType actionType, User user, Subscription subscription, java.math.BigDecimal amountCharged, String currency) {
        HistoryLog historyLog = HistoryLog.builder()
                .user(user)
                .subscription(subscription)
                .actionType(actionType)
                .amountCharged(amountCharged)
                .currency(currency)
                .timestamp(java.time.LocalDateTime.now())
                .subscriptionTitle(subscription != null ? subscription.getTitle() : null)
                .build();
        historyLogRepository.save(historyLog);
    }

    @Override
    public void logHistoryEvent(HistoryActionType actionType, User user, Subscription subscription, java.math.BigDecimal amountCharged, String currency, String subscriptionTitle) {
        HistoryLog historyLog = HistoryLog.builder()
                .user(user)
                .subscription(subscription)
                .actionType(actionType)
                .amountCharged(amountCharged)
                .currency(currency)
                .timestamp(java.time.LocalDateTime.now())
                .subscriptionTitle(subscriptionTitle)
                .build();
        historyLogRepository.save(historyLog);
    }

    @Override
    public Optional<HistoryLog> getHistoryLogById(Long id) {
        return historyLogRepository.findById(id);
    }

    @Override
    public List<HistoryLog> getHistoryLogsByUser(User user) {
        return historyLogRepository.findByUser(user);
    }

    @Override
    public List<HistoryLog> getHistoryLogsByUserId(Long userId) {
        return historyLogRepository.findByUserId(userId);
    }

    @Override
    public List<HistoryLog> getAllHistoryLogs() {
        return historyLogRepository.findAll();
    }

    @Override
    public void deleteHistoryLog(Long id) {
        historyLogRepository.deleteById(id);
    }
}
