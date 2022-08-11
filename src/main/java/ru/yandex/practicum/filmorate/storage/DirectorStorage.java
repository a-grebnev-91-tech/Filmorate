package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    Director addDirector(Director director);

    void addDirectorToFilm(Film film, long directorId);

    Director updateDirector(Director director);

    Optional<Director> getDirector(Long directorId);

    void deleteDirector(Long directorId);

    void deleteDirectorFromFilm(Long filmId, Long directorId);

    List<Director> getAllDirectors();

    List<Director> getDirectorsByFilm(long filmId);

    void deleteAllDirectorsFromFilm(long filmId);

    void updateDirectorToFilm(long filmId, long directorId);
}