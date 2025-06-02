package com.example.subscriptionapp.bot;

import com.example.subscriptionapp.model.TelegramCode;
import com.example.subscriptionapp.model.TelegramLink;
import com.example.subscriptionapp.model.User;
import com.example.subscriptionapp.repository.TelegramCodeRepository;
import com.example.subscriptionapp.repository.TelegramLinkRepository;
import com.example.subscriptionapp.repository.UserRepository;
import com.example.subscriptionapp.service.TelegramLinkService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.springframework.beans.factory.annotation.Value;
import static com.example.subscriptionapp.bot.BotMessages.*;

import java.time.LocalDateTime;

@Component
public class SubscriptionAppBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramCodeRepository codeRepository;
    private final TelegramLinkRepository linkRepository;
    private final TelegramClient telegramClient;
    private final UserRepository userRepository;
    private final TelegramLinkService telegramLinkService;
    private final String botToken;

    public SubscriptionAppBot(
            TelegramCodeRepository codeRepository,
            TelegramLinkRepository linkRepository,
            UserRepository userRepository,
            TelegramLinkService telegramLinkService,
            @Value("${telegrambots.bots[0].token}") String botToken
    ) {
        this.codeRepository = codeRepository;
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
        this.telegramLinkService = telegramLinkService;
        this.botToken = botToken;
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText().trim();

            if (text.equals("/start")) {
                sendMessage(chatId, WELCOME_MESSAGE);
            } else if (text.equals("/help")) {
                sendMessage(chatId, HELP_MESSAGE);
            } else if (text.equals("/unlink")) {
                TelegramLink link = linkRepository.findByChatId(chatId).orElse(null);
                if (link != null) {
                    linkRepository.delete(link);
                    sendMessage(chatId, UNLINK_SUCCESS);
                } else {
                    sendMessage(chatId, UNLINK_NOT_FOUND);
                }
            } else if (text.matches("\\d{6}")) {
                TelegramCode code = codeRepository.findByCode(text).orElse(null);
                if (code == null || code.getExpiresAt().isBefore(LocalDateTime.now())) {
                    sendMessage(chatId, CODE_NOT_FOUND);
                } else {
                    User user = code.getUser();
                    if (user != null && user.getId() != null) {
                        telegramLinkService.linkTelegramAccount(user.getId(), chatId);
                        sendMessage(chatId, LINK_SUCCESS);
                    } else {
                        sendMessage(chatId, USER_NOT_FOUND);
                    }
                }
            } else {
                sendMessage(chatId, DEFAULT_PROMPT);
            }
        }
    }

    public void sendMessage(String chatId, String text) {
        try {
            telegramClient.execute(SendMessage.builder().chatId(chatId).text(text).build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LongPollingSingleThreadUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
} 