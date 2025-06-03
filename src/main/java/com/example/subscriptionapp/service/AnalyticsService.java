package com.example.subscriptionapp.service;

import com.example.subscriptionapp.dto.DashboardDTO;
import com.example.subscriptionapp.dto.MonthlySpendingDTO;
import com.example.subscriptionapp.dto.CategoryBreakdownDTO;
import com.example.subscriptionapp.dto.AnalyticsBarChartItemDTO;
import java.util.List;

public interface AnalyticsService {
    DashboardDTO getDashboardData(Long userId);
    List<MonthlySpendingDTO> getMonthlySpending(Long userId);
    List<CategoryBreakdownDTO> getCategoryBreakdown(Long userId);
    List<AnalyticsBarChartItemDTO> getMostExpensiveSubscriptions(Long userId);
    List<AnalyticsBarChartItemDTO> getLongestSubscriptions(Long userId);
    List<AnalyticsBarChartItemDTO> getMostFundsSpent(Long userId);
    double getYearlySpending(Long userId);
}
