package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long addFilm(Film film) {
        List<Film> allFilms = getFilms();
        if (allFilms.contains(film)) {
            throw new ModelAlreadyExistException("Film already exist");
        } else {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("film")
                    .usingGeneratedKeyColumns("film_id");
            return insert.executeAndReturnKey(film.toMap()).longValue();
        }
    }

    @Override
    public void changeFilm(Film film) {
        String sql =
                "UPDATE film SET film_name = ?, description = ?, release_date = ?, duration_film = ?, mpa_id = ? " +
                        "WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
    }

    @Override
    public List<Film> getFilms() {
        String sql =
                "SELECT * " +
                        "FROM film f, mpa m " +
                        "WHERE f.mpa_id = m.mpa_id";
        return jdbcTemplate.query(sql, FilmDbStorage::mapRowToFilm);
    }

    @Override
    public Film getFilmById(long id) {
        String sqlFilm =
                "SELECT * " +
                        "FROM film f, mpa m " +
                        "WHERE f.mpa_id = m.mpa_id AND film_id = ?";
        return jdbcTemplate.queryForObject(sqlFilm, FilmDbStorage::mapRowToFilm, id);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByYear(Long directorId) {
        String sqlQuery =
                "SELECT * " +
                        "FROM films_directors fd " +
                        "LEFT JOIN film f ON fd.film_id = f.film_id " +
                        "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                        "WHERE fd.director_id = ? " +
                        "ORDER BY release_date";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByLikes(Long directorId) {
        String sqlQuery =
                "SELECT * " +
                        "FROM films_directors fd " +
                        "LEFT JOIN film f ON  fd.film_id = f.film_id " +
                        "LEFT JOIN likes l ON f.film_id = l.film_id " +
                        "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                        "WHERE fd.director_id = ? " +
                        "GROUP BY f.film_id " +
                        "ORDER BY COUNT(l.user_id) DESC";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> getFilms(List<Long> ids) {
        String sqlQueryTemplate =
                "SELECT * " +
                        "FROM film AS f " +
                        "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                        "WHERE film_id IN (%s)";
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sqlQuery = String.format(sqlQueryTemplate, placeholders);
        return jdbcTemplate.query(sqlQuery, ids.toArray(), FilmDbStorage::mapRowToFilm);
    }

    public static Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int idFilm = resultSet.getInt("film_id");
        String name = resultSet.getString("film_name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        int duration = resultSet.getInt("duration_film");
        int rate = resultSet.getInt("rate");
        Film film = new Film(name, description, releaseDate, duration, rate);
        film.setId(idFilm);
        film.setMpa(new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name")));
        return film;
    }

    public List<Film> getPopularFilmsSharedWithFriend(long userId, long friendId) {
        String sql =
                "SELECT * " +
                        "FROM film f, mpa m " +
                        "WHERE  f.mpa_id = m.mpa_id AND film_id IN (SELECT film_id " +
                        "                  FROM likes " +
                        "                  WHERE film_id IN (SELECT film_id " +
                        "                                    FROM likes " +
                        "                                    WHERE user_id = ? " +
                        "                                    INTERSECT " +
                        "                                    SELECT film_id " +
                        "                                    FROM likes " +
                        "                                    WHERE user_id = ?) " +
                        "                  GROUP BY film_id " +
                        "                  ORDER BY count(film_id) DESC)";
        return jdbcTemplate.query(sql, FilmDbStorage::mapRowToFilm, userId, friendId);
    }

    public List<Film> getPopularFilmsByGenre(int limit, long genreId) {
        String sql =
                "SELECT * " +
                        "FROM film f " +
                        "         LEFT JOIN mpa M ON f.mpa_id = m.mpa_id " +
                        "         LEFT JOIN likes l ON f.film_id = l.film_id " +
                        "WHERE f.film_id IN " +
                        "                    (SELECT film_id " +
                        "                     FROM genre_and_film " +
                        "                     WHERE genre_id = ?) " +
                        "GROUP BY f.film_id " +
                        "ORDER BY COUNT(l.user_id) DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sql, FilmDbStorage::mapRowToFilm, genreId, limit);
    }

    public List<Film> getPopularFilmsByYear(int limit, long year) {
        String sql =
                "SELECT * " +
                        "FROM film f " +
                        "         LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                        "         LEFT JOIN likes l ON f.film_id = l.film_id " +
                        "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
                        "GROUP BY f.film_id " +
                        "ORDER BY COUNT(l.user_id) DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sql, FilmDbStorage::mapRowToFilm, year, limit);
    }

    public List<Film> getPopularFilmsByGenreAndYear(int limit, long genreId, long year) {
        String sql =
                "SELECT * " +
                        "FROM film f " +
                        "         LEFT JOIN likes l ON f.film_id = l.film_id " +
                        "         LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                        "         LEFT JOIN genre_and_film g ON f.film_id = g.film_id " +
                        "WHERE g.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ? " +
                        "GROUP BY f.film_id " +
                        "ORDER BY COUNT(l.user_id) DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sql, FilmDbStorage::mapRowToFilm, genreId, year, limit);
    }

    @Override
    public List<Film> searchByDirectors(String query) {
        String sql =
                "SELECT * " +
                        "FROM film f " +
                        "LEFT JOIN films_directors fd ON f.film_id = fd.film_id " +
                        "LEFT JOIN directors d ON fd.director_id = d.director_id " +
                        "LEFT JOIN likes l ON f.film_id = l.film_id " +
                        "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                        "WHERE LOCATE(UPPER(?), UPPER(d.director_name)) " +
                        "ORDER BY f.rate DESC";
        return jdbcTemplate.query(sql, FilmDbStorage::mapRowToFilm, query);
    }

    @Override
    public List<Film> searchByTitles(String query) {
        String sql =
                "SELECT * FROM film " +
                        "LEFT JOIN mpa m ON film.mpa_id = m.mpa_id " +
                        "WHERE LOCATE(UPPER(?), UPPER(description)) " +
                        "ORDER BY rate DESC";
        return jdbcTemplate.query(sql, FilmDbStorage::mapRowToFilm, query);
    }

    @Override
    public void updateFilmRate(long filmId) {
        String sqlQuery =
                "UPDATE film f " +
                        "SET f.rate = " +
                        "(SELECT COUNT(l.user_id) FROM likes l WHERE l.film_id = ?) " +
                        "WHERE f.film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public boolean deleteFilm(long id) {
        String sql = "DELETE FROM film WHERE film_id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }
}