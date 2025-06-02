package com.example.subscriptionapp.service.impl;

import com.example.subscriptionapp.model.Reminder;
import com.example.subscriptionapp.model.Subscription;
import com.example.subscriptionapp.repository.ReminderRepository;
import com.example.subscriptionapp.service.ReminderService;
import com.example.subscriptionapp.service.TelegramNotificationService;
import com.example.subscriptionapp.service.TelegramLinkService;
import com.example.subscriptionapp.model.TelegramLink;
import com.example.subscriptionapp.service.EmailNotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@Transactional
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final TelegramNotificationService telegramNotificationService;
    private final TelegramLinkService telegramLinkService;
    private final EmailNotificationService emailNotificationService;

    // Список напоминаний, рассчитанных для следующего часа
    private List<Reminder> nextHourReminders = new ArrayList<>();
    // Часовой пояс сервера
    private final ZoneId serverZoneId = ZoneOffset.ofHours(3);

    public ReminderServiceImpl(ReminderRepository reminderRepository, TelegramNotificationService telegramNotificationService, TelegramLinkService telegramLinkService, EmailNotificationService emailNotificationService) {
        this.reminderRepository = reminderRepository;
        this.telegramNotificationService = telegramNotificationService;
        this.telegramLinkService = telegramLinkService;
        this.emailNotificationService = emailNotificationService;
    }

    @Override
    public Reminder createReminder(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    @Override
    public Optional<Reminder> getReminderById(Long id) {
        return reminderRepository.findById(id);
    }

    @Override
    public List<Reminder> getRemindersBySubscription(Subscription subscription) {
        return reminderRepository.findBySubscription(subscription);
    }

    @Override
    public Reminder updateReminder(Long id, Reminder reminder) {
        Reminder existing = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found with id " + id));
        existing.setDaysBefore(reminder.getDaysBefore());
        existing.setTimeOfDay(reminder.getTimeOfDay());
        existing.setIsEnabled(reminder.getIsEnabled());
        return reminderRepository.save(existing);
    }

    @Override
    public void deleteReminder(Long id) {
        reminderRepository.deleteById(id);
    }

    // Метод предварительного расчёта напоминаний
    @Scheduled(cron = "0 55 * * * *")
    public void preCalculateRemindersForNextHour() {
        nextHourReminders.clear();
        // Берём текущее серверное время
        LocalDateTime now = LocalDateTime.now(serverZoneId);
        // Рассчитываем начало следующего часа
        LocalDateTime nextHourStart = now.plusHours(1).withMinute(0).withSecond(0).withNano(0);

        // Получаем все включённые напоминания
        List<Reminder> allReminders = reminderRepository.findAll();
        for (Reminder reminder : allReminders) {
            if (!reminder.getIsEnabled()) {
                continue;
            }
            Subscription subscription = reminder.getSubscription();
            // Рассчитываем дату отправки
            LocalDate scheduledSendDate = subscription.getNextPaymentDate().minusDays(reminder.getDaysBefore());
            // Время отправки
            LocalTime scheduledTime = reminder.getTimeOfDay();
            // Собираем LocalDateTime в часовом поясе пользователя
            LocalDateTime scheduledLocal = LocalDateTime.of(scheduledSendDate, scheduledTime);

            // Получаем часовой пояс пользователя из настроек
            String userTimezoneString = subscription.getUser().getDefaultTimezone();
            ZoneId userZoneId;
            try {
                userZoneId = ZoneId.of(userTimezoneString);
            } catch (Exception e) {
                // Если не удалось распарсить, используем часовой пояс сервера
                userZoneId = serverZoneId;
            }

            // Создаем ZonedDateTime в часовом поясе пользователя
            ZonedDateTime userScheduledZdt = scheduledLocal.atZone(userZoneId);
            // Переводим в серверный часовой пояс
            ZonedDateTime scheduledServerZdt = userScheduledZdt.withZoneSameInstant(serverZoneId);

            // Если час запланированного времени совпадает с началом следующего часа, добавляем напоминание
            if (scheduledServerZdt.getHour() == nextHourStart.getHour() &&
                    scheduledServerZdt.toLocalDate().equals(nextHourStart.toLocalDate())) {
                nextHourReminders.add(reminder);
            }
        }
        System.out.println("Предварительно рассчитано " + nextHourReminders.size() +
                " напоминаний для следующего часа (" + nextHourStart + ").");
    }

    // Метод отправки заранее рассчитанных напоминаний.
    @Scheduled(cron = "0 0 * * * *")
    public void sendPreCalculatedReminders() {
        if (nextHourReminders.isEmpty()) {
            System.out.println("Нет напоминаний для отправки в данный час.");
            return;
        }
        for (Reminder reminder : nextHourReminders) {
            sendReminder(reminder);
            // Обновляем время последней отправки, чтобы избежать повторной отправки в этом цикле
            reminder.setLastSentAt(LocalDateTime.now(serverZoneId));
            reminderRepository.save(reminder);
        }
        nextHourReminders.clear();
        System.out.println("Отправлены все предварительно рассчитанные напоминания для текущего часа.");
    }

    private void sendReminder(Reminder reminder) {
        System.out.println("Отправка напоминания для подписки ID: " +
                reminder.getSubscription().getId() + " по времени " + reminder.getTimeOfDay());

        // Get the user from the subscription
        Long userId = reminder.getSubscription().getUser().getId();
        // Check if the user has a linked Telegram account
        Optional<TelegramLink> telegramLink = telegramLinkService.getTelegramLinkByUserId(userId);
        String message = null;
        if (telegramLink.isPresent()) {
            String chatId = telegramLink.get().getChatId();
            Subscription subscription = reminder.getSubscription();
            LocalDate paymentDate = subscription.getNextPaymentDate();
            // Format date with Russian month name
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ru"));
            String formattedDate = paymentDate.format(formatter);
            message = String.format("🔔 Напоминание:\nВаша подписка %s автоматически продлится через %d дней (%s).",
                    subscription.getTitle(),
                    reminder.getDaysBefore(),
                    formattedDate);
            telegramNotificationService.sendMessage(chatId, message);
        }
        // Send email notification (always)
        if (message == null) {
            Subscription subscription = reminder.getSubscription();
            LocalDate paymentDate = subscription.getNextPaymentDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ru"));
            String formattedDate = paymentDate.format(formatter);
            message = String.format("🔔 Напоминание:\nВаша подписка %s автоматически продлится через %d дней (%s).",
                    subscription.getTitle(),
                    reminder.getDaysBefore(),
                    formattedDate);
        }
        String userEmail = reminder.getSubscription().getUser().getEmail();
        String emailSubject = "🔔 Напоминание о подписке: " + reminder.getSubscription().getTitle();
        emailNotificationService.sendEmail(userEmail, emailSubject, message);
    }
}
