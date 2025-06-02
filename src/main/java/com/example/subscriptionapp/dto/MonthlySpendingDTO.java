package com.example.subscriptionapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlySpendingDTO {
    private int year;
    private int month;
    private double totalSpending;
} 