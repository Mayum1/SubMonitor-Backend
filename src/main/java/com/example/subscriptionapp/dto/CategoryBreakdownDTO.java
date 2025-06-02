package com.example.subscriptionapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryBreakdownDTO {
    private String category;
    private double totalSpending;
} 