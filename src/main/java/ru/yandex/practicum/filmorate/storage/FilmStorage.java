package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    long createFilm(Film film);

    void updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(long id);

    boolean deleteFilm(long id);

    List<Film> getFilmsByDirectorSortedByYear(Long directorId);

    List<Film> getFilmsByDirectorSortedByLikes(Long directorId);

    List<Film> getPopularFilmsSharedWithFriend(long userId, long friendId);

    List<Film>  getPopularFilmsByGenre(int limit, long genreId);

    List<Film>  getPopularFilmsByYear(int limit, long year);

    List<Film> getPopularFilmsByGenreAndYear(int limit, long genreId, long year);

    List<Film> getSomeFilms(List<Long> ids);
    
    List<Film> searchByDirectors(String query);

    List<Film> searchByTitles(String query);

    void updateFilmRate(long filmId);
}
