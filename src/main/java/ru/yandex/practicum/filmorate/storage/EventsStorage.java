package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperations;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface EventsStorage {

    List<Event> getEvents(Long id);

    void addEvent(Event event);

    Event getEventById(Long eventId);
}