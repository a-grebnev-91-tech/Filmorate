package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {
    private Long eventId;
    private Long userId;
    private String eventType;
    private String operation;
    private Long timestamp;
    private Long entityId;
}
