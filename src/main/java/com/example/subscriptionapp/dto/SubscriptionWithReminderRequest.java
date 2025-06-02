package com.example.subscriptionapp.dto;

import com.example.subscriptionapp.model.BillingPeriodUnit;
import com.example.subscriptionapp.model.SubscriptionCategory;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SubscriptionWithReminderRequest {
    private String title;
    private Long serviceId;
    private BigDecimal price;
    private String currency;
    private LocalDate firstPaymentDate;
    private Integer billingPeriodValue;
    private BillingPeriodUnit billingPeriodUnit;
    private Boolean autoRenew;
    private String logoOverrideUrl;
    private SubscriptionCategory subscriptionCategory;
    private ReminderDto reminder;

    @Data
    public static class ReminderDto {
        private Integer daysBefore;
        private String timeOfDay; // 'HH:mm'
        private Boolean isEnabled;
    }
} 