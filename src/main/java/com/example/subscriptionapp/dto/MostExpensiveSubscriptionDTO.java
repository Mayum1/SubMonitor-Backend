package com.example.subscriptionapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MostExpensiveSubscriptionDTO {
    private String name;
    private double value;
    private String formatted;
} 