package com.example.subscriptionapp.model;

public enum BillingPeriodUnit {
    DAY("день"),
    MONTH("месяц"),
    YEAR("год");

    private final String description;

    BillingPeriodUnit(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
