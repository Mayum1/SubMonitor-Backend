package com.example.subscriptionapp.controller;

import com.example.subscriptionapp.dto.DashboardDTO;
import com.example.subscriptionapp.dto.MonthlySpendingDTO;
import com.example.subscriptionapp.dto.CategoryBreakdownDTO;
import com.example.subscriptionapp.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final DashboardService dashboardService;

    @Autowired
    public AnalyticsController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary/user/{userId}")
    public DashboardDTO getDashboardSummary(@PathVariable Long userId) {
        return dashboardService.getDashboardData(userId);
    }

    @GetMapping("/monthly-spending/user/{userId}")
    public List<MonthlySpendingDTO> getMonthlySpending(@PathVariable Long userId) {
        return dashboardService.getMonthlySpending(userId);
    }

    @GetMapping("/category-breakdown/user/{userId}")
    public List<CategoryBreakdownDTO> getCategoryBreakdown(@PathVariable Long userId) {
        return dashboardService.getCategoryBreakdown(userId);
    }
} 