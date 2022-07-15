package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface LikesStorage {
    void like(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    Set<Long> getLikes(long filmId);

    List<Film> getPopularFilms(int count);
}
