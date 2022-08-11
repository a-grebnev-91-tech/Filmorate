package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    Genre getGenreById(int id);

    List<Genre> getAllGenres();

    List<Genre> getFilmGenres(long idFilm);

    void addGenresToFilm(Film film, long idFilm);

    void deleteFilmAllGenres(long idFilm);

    void changeFilmGenres(Film film);
}