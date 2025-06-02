package com.example.subscriptionapp.model;

public enum SubscriptionCategory {
    NONE("Без категории"),
    EDUCATION("Образование"),
    VIDEO("Видео"),
    STORAGE("Хранилище"),
    COMMUNICATION("Связь"),
    MUSIC("Музыка"),
    BOOKS("Книги"),
    INTERNET("Интернет"),
    GAMES("Игры"),
    SOCIAL_NETWORKS("Социальные сети"),
    ALL_IN_ONE("Все в одном"),
    APPLICATIONS("Приложения"),
    FINANCE("Финансы"),
    TRANSPORT("Транспорт"),
    OTHER("Другое");

    private final String description;

    SubscriptionCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
