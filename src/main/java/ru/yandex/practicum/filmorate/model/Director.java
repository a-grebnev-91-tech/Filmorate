package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Director {
    private long id;
    private String name;

    public Director(String name) {
        this.name = name;
    }
}