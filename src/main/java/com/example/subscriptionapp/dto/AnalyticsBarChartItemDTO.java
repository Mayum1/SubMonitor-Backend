package com.example.subscriptionapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnalyticsBarChartItemDTO {
    private String name;
    private double value;
    private String formatted;
} 