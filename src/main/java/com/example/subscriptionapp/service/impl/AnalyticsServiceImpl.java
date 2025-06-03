package com.example.subscriptionapp.service.impl;

import com.example.subscriptionapp.dto.DashboardDTO;
import com.example.subscriptionapp.dto.MonthlySpendingDTO;
import com.example.subscriptionapp.dto.CategoryBreakdownDTO;
import com.example.subscriptionapp.model.Subscription;
import com.example.subscriptionapp.model.HistoryLog;
import com.example.subscriptionapp.model.Reminder;
import com.example.subscriptionapp.model.BillingPeriodUnit;
import com.example.subscriptionapp.service.DashboardService;
import com.example.subscriptionapp.service.HistoryLogService;
import com.example.subscriptionapp.service.ReminderService;
import com.example.subscriptionapp.service.SubscriptionService;
import com.example.subscriptionapp.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final SubscriptionService subscriptionService;
    private final HistoryLogService historyLogService;
    private final ReminderService reminderService;
    private final RestTemplate restTemplate;
    private static final String EXCHANGE_RATE_API = "https://open.er-api.com/v6/latest/";
    private static final String BASE_CURRENCY = "RUB";

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public DashboardServiceImpl(SubscriptionService subscriptionService,
                              HistoryLogService historyLogService,
                              ReminderService reminderService) {
        this.subscriptionService = subscriptionService;
        this.historyLogService = historyLogService;
        this.reminderService = reminderService;
        this.restTemplate = new RestTemplate();
    }

    private Map<String, Double> getExchangeRates(String baseCurrency) {
        String url = EXCHANGE_RATE_API + baseCurrency;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response != null && "success".equals(response.get("result"))) {
            return (Map<String, Double>) response.get("rates");
        }
        throw new RuntimeException("Failed to fetch exchange rates");
    }

    private BigDecimal convertToBaseCurrency(BigDecimal amount, String fromCurrency, Map<String, Double> rates) {
        if (fromCurrency.equals(BASE_CURRENCY)) {
            return amount;
        }
        Double rate = rates.get(fromCurrency);
        if (rate == null) {
            throw new RuntimeException("Exchange rate not found for currency: " + fromCurrency);
        }
        return amount.divide(BigDecimal.valueOf(rate), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizeToMonthlyCost(Subscription subscription) {
        BigDecimal price = subscription.getPrice();
        int periodValue = subscription.getBillingPeriodValue();
        BillingPeriodUnit periodUnit = subscription.getBillingPeriodUnit();

        switch (periodUnit) {
            case DAY:
                // Convert daily cost to monthly (assuming 30 days in a month)
                return price.multiply(BigDecimal.valueOf(30)).divide(BigDecimal.valueOf(periodValue), 2, RoundingMode.HALF_UP);
            case MONTH:
                // Already monthly, just divide by period value if it's not 1
                return periodValue == 1 ? price : price.divide(BigDecimal.valueOf(periodValue), 2, RoundingMode.HALF_UP);
            case YEAR:
                // Convert yearly cost to monthly
                return price.divide(BigDecimal.valueOf(12 * periodValue), 2, RoundingMode.HALF_UP);
            default:
                throw new IllegalArgumentException("Unsupported billing period unit: " + periodUnit);
        }
    }

    @Override
    public DashboardDTO getDashboardData(Long userId) {
        DashboardDTO dto = new DashboardDTO();

        List<Subscription> activeSubscriptions = subscriptionService.getAllActiveUserSubscriptions(userId);
        dto.setActiveSubscriptionsCount(activeSubscriptions.size());

        List<Subscription> archivedSubscriptions = subscriptionService.getAllArchivedUserSubscriptions(userId);
        dto.setArchivedSubscriptionsCount(archivedSubscriptions.size());

        Map<String, Double> exchangeRates = getExchangeRates(BASE_CURRENCY);
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        int daysInMonth = now.lengthOfMonth();

        BigDecimal totalSpending = BigDecimal.ZERO;
        Map<String, BigDecimal> spendingByCategory = new HashMap<>();
        for (Subscription sub : activeSubscriptions) {
            int renewals = 0;
            LocalDate start = sub.getFirstPaymentDate();
            if (start == null) continue;
            if (sub.getBillingPeriodUnit() == BillingPeriodUnit.DAY) {
                // Count how many times it will renew in this month
                LocalDate from = start.isAfter(now.withDayOfMonth(1)) ? start : now.withDayOfMonth(1);
                LocalDate to = now.withDayOfMonth(daysInMonth);
                int activeDays = (int) (to.toEpochDay() - from.toEpochDay() + 1);
                renewals = activeDays > 0 ? activeDays / sub.getBillingPeriodValue() : 0;
            } else if (sub.getBillingPeriodUnit() == BillingPeriodUnit.MONTH) {
                // If the renewal falls in this month
                LocalDate next = sub.getNextPaymentDate();
                if (next != null && next.getYear() == currentYear && next.getMonthValue() == currentMonth) {
                    renewals = 1;
                }
            } else if (sub.getBillingPeriodUnit() == BillingPeriodUnit.YEAR) {
                LocalDate next = sub.getNextPaymentDate();
                if (next != null && next.getYear() == currentYear && next.getMonthValue() == currentMonth) {
                    renewals = 1;
                }
            }
            if (renewals > 0) {
                BigDecimal amount = sub.getPrice().multiply(BigDecimal.valueOf(renewals));
                BigDecimal amountInBase = convertToBaseCurrency(amount, sub.getCurrency(), exchangeRates);
                totalSpending = totalSpending.add(amountInBase);
                String category = sub.getSubscriptionCategory() != null ? sub.getSubscriptionCategory().toString() : "OTHER";
                spendingByCategory.put(category, spendingByCategory.getOrDefault(category, BigDecimal.ZERO).add(amountInBase));
            }
        }
        dto.setTotalMonthlySpending(totalSpending);

        // Предстоящие напоминания (только для подписок с оплатой в течение недели)
        LocalDate oneWeekFromNow = LocalDate.now().plusWeeks(1);
        List<Reminder> upcomingReminders = activeSubscriptions.stream()
                .filter(sub -> sub.getNextPaymentDate() != null && 
                             !sub.getNextPaymentDate().isAfter(oneWeekFromNow))
                .flatMap(sub -> reminderService.getRemindersBySubscription(sub).stream())
                .collect(Collectors.toList());
        dto.setUpcomingReminders(upcomingReminders);

        List<HistoryLog> allLogs = historyLogService.getHistoryLogsByUserId(userId);
        List<HistoryLog> latestLogs = allLogs.stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(5)
                .collect(Collectors.toList());
        dto.setLatestHistoryLogs(latestLogs);

        Map<String, Integer> statsByCategory = activeSubscriptions.stream()
                .collect(Collectors.groupingBy(sub -> sub.getSubscriptionCategory().name(), Collectors.summingInt(e -> 1)));
        dto.setSubscriptionsByCategory(statsByCategory);

        return dto;
    }

    @Override
    public List<MonthlySpendingDTO> getMonthlySpending(Long userId) {
        // Use payment history logs for actual spending
        List<HistoryLog> logs = historyLogService.getHistoryLogsByUserId(userId);
        Map<String, Double> exchangeRates = getExchangeRates(BASE_CURRENCY);
        Map<String, BigDecimal> spendingByMonth = new HashMap<>();
        for (HistoryLog log : logs) {
            if (log.getAmountCharged() != null && log.getTimestamp() != null) {
                int year = log.getTimestamp().getYear();
                int month = log.getTimestamp().getMonthValue();
                String key = year + "-" + month;
                BigDecimal amount = log.getAmountCharged();
                String currency = log.getCurrency() != null ? log.getCurrency() : BASE_CURRENCY;
                BigDecimal amountInBase = convertToBaseCurrency(amount, currency, exchangeRates);
                spendingByMonth.put(key, spendingByMonth.getOrDefault(key, BigDecimal.ZERO).add(amountInBase));
            }
        }
        List<MonthlySpendingDTO> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : spendingByMonth.entrySet()) {
            String[] parts = entry.getKey().split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            result.add(new MonthlySpendingDTO(year, month, entry.getValue().doubleValue()));
        }
        result.sort(Comparator.comparing((MonthlySpendingDTO dto) -> dto.getYear() * 100 + dto.getMonth()));
        return result;
    }

    @Override
    public List<CategoryBreakdownDTO> getCategoryBreakdown(Long userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserIdAndIsArchived(userId, false);
        Map<String, Double> exchangeRates = getExchangeRates(BASE_CURRENCY);
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        int daysInMonth = now.lengthOfMonth();
        Map<String, BigDecimal> spendingByCategory = new HashMap<>();
        for (Subscription sub : subscriptions) {
            int renewals = 0;
            LocalDate start = sub.getFirstPaymentDate();
            if (start == null) continue;
            if (sub.getBillingPeriodUnit() == BillingPeriodUnit.DAY) {
                LocalDate from = start.isAfter(now.withDayOfMonth(1)) ? start : now.withDayOfMonth(1);
                LocalDate to = now.withDayOfMonth(daysInMonth);
                int activeDays = (int) (to.toEpochDay() - from.toEpochDay() + 1);
                renewals = activeDays > 0 ? activeDays / sub.getBillingPeriodValue() : 0;
            } else if (sub.getBillingPeriodUnit() == BillingPeriodUnit.MONTH) {
                LocalDate next = sub.getNextPaymentDate();
                if (next != null && next.getYear() == currentYear && next.getMonthValue() == currentMonth) {
                    renewals = 1;
                }
            } else if (sub.getBillingPeriodUnit() == BillingPeriodUnit.YEAR) {
                LocalDate next = sub.getNextPaymentDate();
                if (next != null && next.getYear() == currentYear && next.getMonthValue() == currentMonth) {
                    renewals = 1;
                }
            }
            if (renewals > 0) {
                BigDecimal amount = sub.getPrice().multiply(BigDecimal.valueOf(renewals));
                BigDecimal amountInBase = convertToBaseCurrency(amount, sub.getCurrency(), exchangeRates);
                String category = sub.getSubscriptionCategory() != null ? sub.getSubscriptionCategory().toString() : "OTHER";
                spendingByCategory.put(category, spendingByCategory.getOrDefault(category, BigDecimal.ZERO).add(amountInBase));
            }
        }
        List<CategoryBreakdownDTO> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : spendingByCategory.entrySet()) {
            result.add(new CategoryBreakdownDTO(entry.getKey(), entry.getValue().doubleValue()));
        }
        return result;
    }
}
