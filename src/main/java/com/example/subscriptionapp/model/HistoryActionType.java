package com.example.subscriptionapp.model;

public enum HistoryActionType {
    CREATED("Подписка создана"),
    UPDATED("Подписка обновлена"),
    DELETED("Подписка удалена"),
    ARCHIVED("Подписка архивирована"),
    RESTORED("Подписка восстановлена"),
    RENEWED("Продление подписки");

    private final String description;

    HistoryActionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
