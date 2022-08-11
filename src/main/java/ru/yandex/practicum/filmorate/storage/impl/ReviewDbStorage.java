package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long addReview(Review review) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        return insert.executeAndReturnKey(review.toMap()).longValue();
    }

    @Override
    public void changeReview(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? " +
                "WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
    }

    @Override
    public void deleteReview(long id) {
        String sql = "DELETE FROM reviews " +
                "WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Review getReviewById(long id) {
        String sql = "SELECT * FROM reviews " +
                "WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sql, ReviewDbStorage::mapRowToReview, id);
    }

    @Override
    public List<Review> getReviewByFilmId(long filmId, int count) {
        String sql = "SELECT * FROM reviews " +
                "WHERE film_id = ? " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, ReviewDbStorage::mapRowToReview, filmId, count);
    }

    @Override
    public List<Review> getCountReview(int count) {
        String sql = "SELECT * FROM reviews " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, ReviewDbStorage::mapRowToReview, count);
    }

    @Override
    public List<Review> getAllReview() {
        String sql = "SELECT * FROM reviews";
        return jdbcTemplate.query(sql, ReviewDbStorage::mapRowToReview);
    }

    @Override
    public void addLike(long id, long userId, boolean isLike) {
        String sql = "MERGE INTO review_likes (review_id, user_id, is_like) KEY (review_id, user_id) VALUES ( ?, ?, ? )";
        jdbcTemplate.update(sql, id, userId, isLike);
        updateUseful(id);
    }

    @Override
    public void deleteLike(long id, long userId, boolean isLike) {
        String sql = "DELETE FROM review_likes " +
                "WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
        updateUseful(id);
    }

    private void updateUseful(long reviewId) {
        String sql = "UPDATE reviews SET useful = ? " +
                "WHERE review_id = ?";
        jdbcTemplate.update(sql, calculateUseful(reviewId), reviewId);
    }

    private int calculateUseful(long reviewId) {
        String sqlQuery = "SELECT " +
                "(SELECT COUNT (*) FROM review_likes WHERE review_id = ? AND is_like) - " +
                "(SELECT COUNT (*) FROM review_likes WHERE review_id = ? AND NOT is_like)";
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class, reviewId, reviewId);
    }

    public static Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        long id = resultSet.getLong("review_id");
        String content = resultSet.getString("content");
        boolean isPositive = resultSet.getBoolean("is_positive");
        long userId = resultSet.getLong("user_id");
        long filmId = resultSet.getLong("film_id");
        int useful = resultSet.getInt("useful");
        return new Review(id, content, isPositive, userId, filmId, useful);
    }
}