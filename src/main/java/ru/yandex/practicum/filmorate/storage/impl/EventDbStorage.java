package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperations;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.storage.EventsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class EventDbStorage implements EventsStorage {
    private static final String GET_EVENTS = "SELECT * FROM events WHERE event_user_id = ?";
    private static final String GET_EVENT_BY_ID = "SELECT * FROM events WHERE event_id = ?";
    private final JdbcTemplate jdbcTemplate;

    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> getEvents(Long userId) {
        log.info("Start EventDbStorage.getEvents userId:{}.", userId);
        List<Event> events = jdbcTemplate.query(GET_EVENTS, this::mapRowToEvent, userId);
        log.info("End EventDbStorage.getEvents userId:{}.List<Event> events.size = {}", userId, events.size());
        return events;
    }

    @Override
    public void addEvent(Event event) {
        log.info("Start EventDbStorage.addEvent event:{}.", event);
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("event_id");
        long eventId = insert.executeAndReturnKey(event.toMapEvent()).longValue();
        event.setEventId(eventId);
        log.info("End EventDbStorage.addEvent event was added:{}.", event);
    }

    private Event mapRowToEvent(ResultSet rs, int rowNum) throws SQLException {
        return new Event(
                rs.getLong("event_id"),
                rs.getLong("event_user_id"),
                rs.getLong("entity_id"),
                EventType.valueOf(rs.getString("event_type")),
                EventOperations.valueOf(rs.getString("event_operation")),
                rs.getLong("time_stamp")
        );
    }
}