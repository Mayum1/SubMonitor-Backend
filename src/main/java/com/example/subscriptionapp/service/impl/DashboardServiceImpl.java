package com.example.subscriptionapp.service.impl;

import com.example.subscriptionapp.dto.DashboardDTO;
import com.example.subscriptionapp.dto.MonthlySpendingDTO;
import com.example.subscriptionapp.dto.CategoryBreakdownDTO;
import com.example.subscriptionapp.model.Subscription;
import com.example.subscriptionapp.model.HistoryLog;
import com.example.subscriptionapp.model.Reminder;
import com.example.subscriptionapp.service.DashboardService;
import com.example.subscriptionapp.service.HistoryLogService;
import com.example.subscriptionapp.service.ReminderService;
import com.example.subscriptionapp.service.SubscriptionService;
import com.example.subscriptionapp.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final SubscriptionService subscriptionService;
    private final HistoryLogService historyLogService;
    private final ReminderService reminderService;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public DashboardServiceImpl(SubscriptionService subscriptionService,
                                HistoryLogService historyLogService,
                                ReminderService reminderService) {
        this.subscriptionService = subscriptionService;
        this.historyLogService = historyLogService;
        this.reminderService = reminderService;
    }

    @Override
    public DashboardDTO getDashboardData(Long userId) {
        DashboardDTO dto = new DashboardDTO();

        // Получаем активные подписки (isArchived = false) для пользователя
        List<Subscription> activeSubscriptions = subscriptionService.getAllActiveUserSubscriptions(userId);
        dto.setActiveSubscriptionsCount(activeSubscriptions.size());

        // Получаем архивированные подписки
        List<Subscription> archivedSubscriptions = subscriptionService.getAllArchivedUserSubscriptions(userId);
        dto.setArchivedSubscriptionsCount(archivedSubscriptions.size());

        // Суммарный месячный расход (можно усреднять/складывать цены активных подписок)
        BigDecimal totalSpending = activeSubscriptions.stream()
                .map(Subscription::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalMonthlySpending(totalSpending);

        // Предстоящие напоминания (например, все напоминания для подписок, у которых ближайшая дата платежа в будущем)
        // Здесь для примера просто получаем все напоминания для активных подписок
        List<Reminder> upcomingReminders = activeSubscriptions.stream()
                .flatMap(sub -> reminderService.getRemindersBySubscription(sub).stream())
                .collect(Collectors.toList());
        dto.setUpcomingReminders(upcomingReminders);

        // Последние 5 записей истории для пользователя
        List<HistoryLog> allLogs = historyLogService.getHistoryLogsByUserId(userId);
        List<HistoryLog> latestLogs = allLogs.stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(5)
                .collect(Collectors.toList());
        dto.setLatestHistoryLogs(latestLogs);

        // Статистика по категориям подписок
        Map<String, Integer> statsByCategory = activeSubscriptions.stream()
                .collect(Collectors.groupingBy(sub -> sub.getSubscriptionCategory().name(), Collectors.summingInt(e -> 1)));
        dto.setSubscriptionsByCategory(statsByCategory);

        return dto;
    }

    @Override
    public List<MonthlySpendingDTO> getMonthlySpending(Long userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserIdAndIsArchived(userId, false);
        Map<String, BigDecimal> spendingByMonth = new HashMap<>();
        for (Subscription sub : subscriptions) {
            if (sub.getNextPaymentDate() != null) {
                LocalDate date = sub.getNextPaymentDate();
                String key = date.getYear() + "-" + date.getMonthValue();
                spendingByMonth.put(key, spendingByMonth.getOrDefault(key, BigDecimal.ZERO).add(sub.getPrice()));
            }
        }
        List<MonthlySpendingDTO> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : spendingByMonth.entrySet()) {
            String[] parts = entry.getKey().split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            result.add(new MonthlySpendingDTO(year, month, entry.getValue().doubleValue()));
        }
        result.sort(Comparator.comparing((MonthlySpendingDTO dto) -> dto.getYear() * 100 + dto.getMonth()));
        return result;
    }

    @Override
    public List<CategoryBreakdownDTO> getCategoryBreakdown(Long userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserIdAndIsArchived(userId, false);
        Map<String, BigDecimal> spendingByCategory = new HashMap<>();
        for (Subscription sub : subscriptions) {
            String category = sub.getSubscriptionCategory() != null ? sub.getSubscriptionCategory().toString() : "OTHER";
            spendingByCategory.put(category, spendingByCategory.getOrDefault(category, BigDecimal.ZERO).add(sub.getPrice()));
        }
        List<CategoryBreakdownDTO> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : spendingByCategory.entrySet()) {
            result.add(new CategoryBreakdownDTO(entry.getKey(), entry.getValue().doubleValue()));
        }
        return result;
    }
}
