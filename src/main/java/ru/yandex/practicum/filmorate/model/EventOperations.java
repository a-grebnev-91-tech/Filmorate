package ru.yandex.practicum.filmorate.model;

public enum EventOperations {
    REMOVE("REMOVE"),
    ADD("ADD"),
    UPDATE("UPDATE");
    private final String title;

    EventOperations(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}