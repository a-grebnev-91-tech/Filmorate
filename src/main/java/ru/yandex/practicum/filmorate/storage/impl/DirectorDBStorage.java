package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
public class DirectorDBStorage implements DirectorStorage {
    private static final String GET_ALL_DIRECTORS = "SELECT * FROM directors";
    private static final String UPDATE_DIRECTORS = "UPDATE directors SET director_name = ? WHERE director_id = ? ";
    private static final String GET_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE director_id = ?";
    private static final String DELETE_DIRECTOR_BY_ID = "DELETE FROM directors WHERE director_id = ?";
    private static final String INSERT_DIR_TO_FILM = "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)";
    private static final String UPDATE_DIR_TO_FILM = "MERGE INTO films_directors (film_id, director_id) VALUES (?, ?)";
    private static final String DELETE_DIRECTOR_FROM_FILM =
            "DELETE FROM films_directors WHERE film_id = ? AND director_id = ?";
    private static final String DELETE_ALL_DIRECTORS_FROM_FILM = "DELETE FROM films_directors WHERE FILM_ID = ?";
    private static final String GET_DIRECTORS_FROM_FILM = "SELECT d.director_id, d.director_name " +
            "FROM directors AS d " +
            "LEFT JOIN films_directors AS fd ON d.director_id = fd.director_id WHERE fd.film_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getAllDirectors() {
        log.info("Start DirectorStorage. Method getAllDirectors.");
        return jdbcTemplate.query(GET_ALL_DIRECTORS, (this::mapRowToDirector));
    }

    @Override
    public Director addDirector(Director director) {
        log.info("Start DirectorStorage. Method addDirector. director:{}", director);
        Map<String, Object> keys = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingColumns("director_name")
                .usingGeneratedKeyColumns("director_id")
                .executeAndReturnKeyHolder(Map.of("director_name", director.getName()))
                .getKeys();
        director.setId((Long) ((Objects.requireNonNull(keys))).get("director_id"));
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        log.info("Start DirectorStorage. Method updateDirector. director:{}", director);
        jdbcTemplate.update(UPDATE_DIRECTORS, director.getName(), director.getId());
        return director;
    }

    @Override
    public Optional<Director> getDirector(Long directorId) {
        log.info("Start DirectorStorage. Method getDirector. directorId:{}", directorId);
        List<Director> directors = jdbcTemplate.query(GET_DIRECTOR_BY_ID, this::mapRowToDirector, directorId);
        if (directors.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(directors.get(0));
        }
    }

    @Override
    public void deleteDirector(Long directorId) {
        log.info("Start DirectorStorage. deleteDirector с directorId: {} ", directorId);
        jdbcTemplate.update(DELETE_DIRECTOR_BY_ID, directorId);
    }

    private Director mapRowToDirector(ResultSet result, int rowNum) throws SQLException {
        int id = result.getInt("director_id");
        String name = result.getString("director_name");
        return new Director(id, name);
    }

    @Override
    public void addDirectorToFilm(Film film, long directorId) {
        log.info("Start DirectorDbStorage addDirectorToFilm directorId: {} film {}", directorId, film);
        for (Director director : film.getDirectors()) {
            jdbcTemplate.update(INSERT_DIR_TO_FILM, film.getId(), director.getId());
        }
    }

    @Override
    public void deleteDirectorFromFilm(Long filmId, Long directorId) {
        log.info("Start DirectorDbStorage deleteDirectorFromFilm filmId {}, directorId {}", filmId, directorId);
        jdbcTemplate.update(DELETE_DIRECTOR_FROM_FILM, filmId, directorId);
    }

    @Override
    public List<Director> getDirectorsByFilm(long filmId) {
        log.info("Start DirectorDbStorage getFilmDirectors filmId {}", filmId);
        return jdbcTemplate.query(GET_DIRECTORS_FROM_FILM, this::mapRowToDirector, filmId);
    }

    @Override
    public void deleteAllDirectorsFromFilm(long filmId) {
        log.info("Start DirectorDbStorage deleteAllDirectorsFromFilm  filmId {}", filmId);
        jdbcTemplate.update(DELETE_ALL_DIRECTORS_FROM_FILM, filmId);
    }

    @Override
    public void updateDirectorToFilm(long filmId, long directorId) {
        log.info("Start DirectorDbStorage updateDirectorToFilm  filmId {}, directorId {}", filmId, directorId);
        jdbcTemplate.update(UPDATE_DIR_TO_FILM, filmId, directorId);
    }
}