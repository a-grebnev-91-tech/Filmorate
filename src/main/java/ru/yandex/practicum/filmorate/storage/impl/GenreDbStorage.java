package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(int id) {
        String sqlQuery =
                "SELECT * " +
                        "FROM genres " +
                        "WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public List<Genre> getFilmGenres(long idFilm) {
        String sqlQuery =
                "SELECT g.genre_id, g.name " +
                        "FROM genres AS g " +
                        "LEFT JOIN films_genres AS fg ON g.genre_id = fg.genre_id " +
                        "WHERE fg.film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, idFilm);
    }

    //todo refact this
    @Override
    public void addGenresToFilm(Film film, long idFilm) {
        String sqlQuery =
                "INSERT INTO films_genres (genre_id, film_id) " +
                "VALUES (?, ?)";
        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                jdbcTemplate.update(sqlQuery, genre.getId(), idFilm);
            }
        }
    }

    @Override
    public void deleteFilmAllGenres(long idFilm) {
        String sqlQuery =
                "DELETE FROM FILMS_GENRES " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, idFilm);
    }

    //todo refact this
    @Override
    public void changeFilmGenres(Film film) {
        if (film.getGenres().size() != 0) {
            deleteFilmAllGenres(film.getId());
            addGenresToFilm(film, film.getId());
        } else {
            deleteFilmAllGenres(film.getId());
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int i) throws SQLException {
        int id = resultSet.getInt("genre_id");
        String name = resultSet.getString("name");
        return new Genre(id, name);
    }
}