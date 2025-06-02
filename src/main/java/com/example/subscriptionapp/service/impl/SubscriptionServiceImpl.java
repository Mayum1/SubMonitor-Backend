package com.example.subscriptionapp.service.impl;

import com.example.subscriptionapp.model.*;
import com.example.subscriptionapp.repository.SubscriptionRepository;
import com.example.subscriptionapp.repository.HistoryLogRepository;
import com.example.subscriptionapp.service.HistoryLogService;
import com.example.subscriptionapp.service.SubscriptionService;
import com.example.subscriptionapp.service.TelegramNotificationService;
import com.example.subscriptionapp.service.TelegramLinkService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final HistoryLogService historyLogService;
    private final HistoryLogRepository historyLogRepository;
    private final TelegramNotificationService telegramNotificationService;
    private final TelegramLinkService telegramLinkService;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, HistoryLogService historyLogService, HistoryLogRepository historyLogRepository, TelegramNotificationService telegramNotificationService, TelegramLinkService telegramLinkService) {
        this.subscriptionRepository = subscriptionRepository;
        this.historyLogService = historyLogService;
        this.historyLogRepository = historyLogRepository;
        this.telegramNotificationService = telegramNotificationService;
        this.telegramLinkService = telegramLinkService;
    }

    @Override
    public Subscription createSubscription(Subscription subscription) {
        // Проверяем обязательные поля
        if (subscription.getFirstPaymentDate() == null) {
            throw new IllegalArgumentException("Дата первого платежа обязательна");
        }
        if (subscription.getBillingPeriodValue() == null || subscription.getBillingPeriodUnit() == null) {
            throw new IllegalArgumentException("Период оплаты обязателен");
        }
        if (subscription.getTitle() == null || subscription.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Название подписки обязательно");
        }

        // Устанавливаем дефолтные значения, если они не заданы
        if (subscription.getIsArchived() == null) {
            subscription.setIsArchived(false);
        }
        if (subscription.getAutoRenew() == null) {
            subscription.setAutoRenew(true);
        }
        // Вычисляем дату следующего платежа, исходя из даты первого платежа и периода оплаты
        LocalDate firstPaymentDate = subscription.getFirstPaymentDate();
        int periodValue = subscription.getBillingPeriodValue();
        BillingPeriodUnit periodUnit = subscription.getBillingPeriodUnit();

        LocalDate nextPaymentDate;
        switch (periodUnit) {
            case DAY:
                nextPaymentDate = firstPaymentDate.plusDays(periodValue);
                break;
            case MONTH:
                nextPaymentDate = firstPaymentDate.plusMonths(periodValue);
                break;
            case YEAR:
                nextPaymentDate = firstPaymentDate.plusYears(periodValue);
                break;
            default:
                nextPaymentDate = firstPaymentDate;
        }
        subscription.setNextPaymentDate(nextPaymentDate);
        if (subscription.getSubscriptionCategory() == null) {
            // Если категория не указана, устанавливаем значение по умолчанию
            subscription.setSubscriptionCategory(SubscriptionCategory.NONE);
        }
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        historyLogService.logHistoryEvent(
                HistoryActionType.CREATED,
                subscription.getUser(),
                savedSubscription,
                subscription.getPrice(),
                subscription.getCurrency()
        );
        return savedSubscription;
    }

    @Override
    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

    @Override
    public List<Subscription> getAllActiveUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserIdAndIsArchived(userId, false);
    }

    @Override
    public List<Subscription> getAllArchivedUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserIdAndIsArchived(userId, true);
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    @Override
    public Subscription archiveSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id " + id));
        subscription.setIsArchived(true);
        Subscription archived = subscriptionRepository.save(subscription);
        historyLogService.logHistoryEvent(
                HistoryActionType.ARCHIVED,
                subscription.getUser(),
                archived,
                null,
                null
        );
        return archived;
    }

    @Override
    public Subscription restoreSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id " + id));
        subscription.setIsArchived(false);
        Subscription restored = subscriptionRepository.save(subscription);
        historyLogService.logHistoryEvent(
                HistoryActionType.RESTORED,
                subscription.getUser(),
                restored,
                subscription.getPrice(),
                subscription.getCurrency()
        );
        return restored;
    }

    @Override
    public Subscription updateSubscription(Long id, Subscription subscription) {
        Subscription existing = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id " + id));
        existing.setTitle(subscription.getTitle());
        existing.setPrice(subscription.getPrice());
        existing.setCurrency(subscription.getCurrency());
        existing.setFirstPaymentDate(subscription.getFirstPaymentDate());
        existing.setNextPaymentDate(subscription.getNextPaymentDate());
        existing.setBillingPeriodValue(subscription.getBillingPeriodValue());
        existing.setBillingPeriodUnit(subscription.getBillingPeriodUnit());
        existing.setAutoRenew(subscription.getAutoRenew());
        existing.setIsArchived(subscription.getIsArchived());
        existing.setLogoOverrideUrl(subscription.getLogoOverrideUrl());
        existing.setSubscriptionCategory(subscription.getSubscriptionCategory());

        Subscription updated = subscriptionRepository.save(existing);
        historyLogService.logHistoryEvent(
                HistoryActionType.UPDATED,
                existing.getUser(),
                updated,
                null,
                null
        );
        return updated;
    }

    @Override
    public void deleteSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id " + id));
        // Set subscription field to null in all related history logs
        List<HistoryLog> logs = historyLogRepository.findBySubscription(subscription);
        for (HistoryLog log : logs) {
            log.setSubscription(null);
        }
        historyLogRepository.saveAll(logs);
        // Now delete the subscription (reminders will be deleted by cascade)
        subscriptionRepository.deleteById(id);
        historyLogService.logHistoryEvent(
                HistoryActionType.DELETED,
                subscription.getUser(),
                null,
                null,
                null,
                subscription.getTitle()
        );
    }

    // Обновление подписок с автопродлением
    @Scheduled(cron = "0 0 0 * * *")
    public void processRenewals() {
        // Получаем подписки, у которых автопродление включено и дата следующего платежа наступила или прошла
        List<Subscription> dueSubscriptions = subscriptionRepository.findAll().stream()
                .filter(sub -> sub.getAutoRenew()
                        && !sub.getNextPaymentDate().isAfter(LocalDate.now()))
                .toList();

        for (Subscription subscription : dueSubscriptions) {
            try {
                renewSubscription(subscription);
            } catch (Exception e) {
                // Логирование ошибки, уведомление администратора или попытка повторной обработки
                e.printStackTrace();
            }
        }
    }

    // Продление подписки
    private void renewSubscription(Subscription subscription) {
        LocalDate currentNextPaymentDate = subscription.getNextPaymentDate();
        LocalDate newNextPaymentDate = calculateNewPaymentDate(currentNextPaymentDate,
                subscription.getBillingPeriodValue(), subscription.getBillingPeriodUnit());

        subscription.setNextPaymentDate(newNextPaymentDate);
        subscriptionRepository.save(subscription);

        // Логируем событие продления
        historyLogService.logHistoryEvent(
                HistoryActionType.RENEWED,
                subscription.getUser(),
                subscription,
                subscription.getPrice(),
                subscription.getCurrency()
        );

        // Send Telegram notification if the user has a linked Telegram account
        Long userId = subscription.getUser().getId();
        Optional<TelegramLink> telegramLink = telegramLinkService.getTelegramLinkByUserId(userId);
        if (telegramLink.isPresent()) {
            String chatId = telegramLink.get().getChatId();
            String message = String.format("🔔 Напоминание:\nВаша подписка %s была продлена. Следующий платеж: %d %s %d.",
                    subscription.getTitle(),
                    newNextPaymentDate.getDayOfMonth(),
                    newNextPaymentDate.getMonth().toString().toLowerCase(),
                    newNextPaymentDate.getYear());
            telegramNotificationService.sendMessage(chatId, message);
        }
    }

    // Расчет новой даты следующего платежа
    private LocalDate calculateNewPaymentDate(LocalDate current, int periodValue, BillingPeriodUnit unit) {
        return switch (unit) {
            case DAY -> current.plusDays(periodValue);
            case MONTH -> current.plusMonths(periodValue);
            case YEAR -> current.plusYears(periodValue);
            default -> throw new IllegalArgumentException("Неизвестная единица периода: " + unit);
        };
    }
}
