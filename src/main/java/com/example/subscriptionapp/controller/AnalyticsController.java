package com.example.subscriptionapp.controller;

import com.example.subscriptionapp.dto.DashboardDTO;
import com.example.subscriptionapp.dto.MonthlySpendingDTO;
import com.example.subscriptionapp.dto.CategoryBreakdownDTO;
import com.example.subscriptionapp.dto.AnalyticsBarChartItemDTO;
import com.example.subscriptionapp.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary/user/{userId}")
    public DashboardDTO getDashboardSummary(@PathVariable Long userId) {
        return analyticsService.getDashboardData(userId);
    }

    @GetMapping("/monthly-spending/user/{userId}")
    public List<MonthlySpendingDTO> getMonthlySpending(@PathVariable Long userId) {
        return analyticsService.getMonthlySpending(userId);
    }

    @GetMapping("/category-breakdown/user/{userId}")
    public List<CategoryBreakdownDTO> getCategoryBreakdown(@PathVariable Long userId) {
        return analyticsService.getCategoryBreakdown(userId);
    }

    @GetMapping("/most-expensive/user/{userId}")
    public List<AnalyticsBarChartItemDTO> getMostExpensiveSubscriptions(@PathVariable Long userId) {
        return analyticsService.getMostExpensiveSubscriptions(userId);
    }

    @GetMapping("/longest/user/{userId}")
    public List<AnalyticsBarChartItemDTO> getLongestSubscriptions(@PathVariable Long userId) {
        return analyticsService.getLongestSubscriptions(userId);
    }

    @GetMapping("/most-funds-spent/user/{userId}")
    public List<AnalyticsBarChartItemDTO> getMostFundsSpent(@PathVariable Long userId) {
        return analyticsService.getMostFundsSpent(userId);
    }

    @GetMapping("/yearly-spending/user/{userId}")
    public double getYearlySpending(@PathVariable Long userId) {
        return analyticsService.getYearlySpending(userId);
    }
} 