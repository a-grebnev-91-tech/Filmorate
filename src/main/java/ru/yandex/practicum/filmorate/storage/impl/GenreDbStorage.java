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
        String sql = "SELECT * " +
                "FROM genre " +
                "WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    @Override
    public List<Genre> getFilmGenres(long idFilm) {
        String sql = "SELECT g.genre_id, g.name " +
                "FROM genre AS g " +
                "LEFT JOIN genre_and_film AS gaf ON g.genre_id = gaf.genre_id " +
                "WHERE gaf.film_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToGenre, idFilm);
    }

    @Override
    public void addGenresToFilm(Film film, long idFilm) {
        String sql = "INSERT INTO genre_and_film (genre_id, film_id) " +
                "VALUES (?, ?)";
        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                jdbcTemplate.update(sql, genre.getId(), idFilm);
            }
        }
    }

    @Override
    public void deleteFilmAllGenres(long idFilm) {
        String sql = "DELETE FROM genre_and_film " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sql, idFilm);
    }

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
        int idGenre = resultSet.getInt("genre_id");
        String name = resultSet.getString("name");
        return new Genre(idGenre, name);
    }
}