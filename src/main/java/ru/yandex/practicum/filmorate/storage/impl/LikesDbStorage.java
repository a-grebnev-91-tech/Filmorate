package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sqlQuery =
                "INSERT INTO films_likes (film_id, user_id)" +
                        "VALUES(?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        updateRate(filmId);
    }

    @Override
    public Set<Long> getLikes(long filmId) {
        String sqlQuery =
                "SELECT user_id " +
                        "FROM films_likes " +
                        "WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.query(sqlQuery, LikesDbStorage::mapRowToLong, filmId);
        return new HashSet<>(likes);
    }

    //TODO move to film dao
    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery =
                "SELECT * FROM films f " +
                        "LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id " +
                        "ORDER BY f.rate DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm, count);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sqlQuery =
                "DELETE FROM films_likes " +
                        "WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        updateRate(filmId);
    }

    //todo move to film dao
    @Override
    public void updateRate(long filmId) {
        String sqlQuery =
                "UPDATE films f SET rate = " +
                        "(SELECT COUNT(l.user_id) FROM films_likes l WHERE l.film_id = f.film_id) " +
                        "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public List<Long> getRecommendations(long userId) {
        String sqlQuery =
                "SELECT film_id FROM films_likes WHERE user_id = " +
                        "(SELECT user_id FROM films_likes WHERE film_id IN " +
                        "(SELECT film_id FROM films_likes WHERE user_id = ?) AND user_id <> ? " +
                        "GROUP BY user_id ORDER BY COUNT(film_id) DESC LIMIT 1) " +
                        "AND film_id NOT IN (SELECT film_id FROM films_likes WHERE user_id = ?)";
        return jdbcTemplate.query(sqlQuery, this::makeFilmId, userId, userId, userId);
    }

    private static long mapRowToLong(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("user_id");
    }

    //todo wtf
    private long makeFilmId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("film_id");
    }
}