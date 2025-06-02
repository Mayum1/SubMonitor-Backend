package com.example.subscriptionapp.dto;

import com.example.subscriptionapp.model.HistoryLog;
import com.example.subscriptionapp.model.Reminder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardDTO {
    private int activeSubscriptionsCount;
    private int archivedSubscriptionsCount;
    private BigDecimal totalMonthlySpending;
    private List<Reminder> upcomingReminders;
    private List<HistoryLog> latestHistoryLogs;
    private Map<String, Integer> subscriptionsByCategory;
}
