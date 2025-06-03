package com.example.subscriptionapp.service;

import com.example.subscriptionapp.dto.DashboardDTO;
import com.example.subscriptionapp.dto.MonthlySpendingDTO;
import com.example.subscriptionapp.dto.CategoryBreakdownDTO;
import java.util.List;

public interface DashboardService {
    DashboardDTO getDashboardData(Long userId);
    List<MonthlySpendingDTO> getMonthlySpending(Long userId);
    List<CategoryBreakdownDTO> getCategoryBreakdown(Long userId);
}
