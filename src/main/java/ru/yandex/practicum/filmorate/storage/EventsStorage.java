package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventsStorage {

    List<Event> getEvents(Long id);

    void addEvent(Event event);
}