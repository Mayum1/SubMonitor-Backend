package com.example.subscriptionapp.bot;

class BotMessages {
    static final String WELCOME_MESSAGE = "👋 Добро пожаловать в SubMonitor!\n\n" +
            "Этот бот поможет вам получать напоминания о подписках и управлять ими прямо в Telegram.\n" +
            "\nЧтобы связать ваш аккаунт, сгенерируйте код в настройках приложения и отправьте его сюда.\n" +
            "\nДоступные команды:\n/help — список команд\n/unlink — отвязать Telegram\n\nЕсли у вас есть вопросы, используйте /help.";

    static final String HELP_MESSAGE = "🤖 Доступные команды:\n" +
            "/start — приветствие и инструкция\n" +
            "/help — список команд\n" +
            "/unlink — отвязать Telegram от аккаунта\n" +
            "Отправьте 6-значный код для привязки аккаунта.";

    static final String UNLINK_SUCCESS = "Ваш Telegram успешно отвязан от аккаунта. Вы больше не будете получать уведомления.";
    static final String UNLINK_NOT_FOUND = "Ваш Telegram не был привязан к аккаунту.";
    static final String CODE_NOT_FOUND = "Код не найден или истёк. Пожалуйста, сгенерируйте новый код в приложении.";
    static final String LINK_SUCCESS = "Ваш Telegram успешно привязан к аккаунту!";
    static final String USER_NOT_FOUND = "Ошибка: пользователь не найден.";
    static final String DEFAULT_PROMPT = "Пожалуйста, отправьте 6-значный код из приложения или используйте /help для списка команд.";
} 