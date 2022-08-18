package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
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
    public long createReview(Review review) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        return insert.executeAndReturnKey(review.toMap()).longValue();
    }

    @Override
    public void deleteReview(long id) {
        String sqlQuery =
                "DELETE FROM reviews " +
                        "WHERE review_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Review> getAllReviews() {
        String sqlQuery = "SELECT * FROM reviews ";
        return jdbcTemplate.query(sqlQuery, ReviewDbStorage::mapRowToReview);
    }

    @Override
    public List<Review> getFilmsReviews(long filmId, int count) {
        String sqlQuery =
                "SELECT * FROM reviews " +
                        "WHERE film_id = ? " +
                        "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, ReviewDbStorage::mapRowToReview, filmId, count);
    }

    @Override
    public Review getReview(long id) {
        String sqlQuery =
                "SELECT * FROM reviews " +
                        "WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, ReviewDbStorage::mapRowToReview, id);
    }

    @Override
    public List<Review> getSomeReviews(int count) {
        String sqlQuery =
                "SELECT * FROM reviews " +
                        "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, ReviewDbStorage::mapRowToReview, count);
    }

    @Override
    public void updateReview(Review review) {
        String sqlQuery =
                "UPDATE reviews " +
                        "SET content = ?, is_positive = ? " +
                        "WHERE review_id = ?";
        jdbcTemplate.update(sqlQuery, review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
    }

    //todo move to review likes storage
    @Override
    public void addLike(long id, long userId, boolean isLike) {
        String sqlQuery =
                "MERGE INTO reviews_likes (review_id, user_id, is_like) KEY (review_id, user_id) VALUES ( ?, ?, ? )";
        jdbcTemplate.update(sqlQuery, id, userId, isLike);
        updateUseful(id);
    }

    //todo move to review likes storage
    @Override
    public void deleteLike(long id, long userId, boolean isLike) {
        String sqlQuery = "DELETE FROM reviews_likes " +
                "WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
        updateUseful(id);
    }

    //todo check it
    private void updateUseful(long reviewId) {
        String sqlQuery = "UPDATE reviews SET useful = ? " +
                "WHERE review_id = ?";
        jdbcTemplate.update(sqlQuery, calculateUseful(reviewId), reviewId);
    }

    //todo check this
    private int calculateUseful(long reviewId) {
        String sqlQuery = "SELECT " +
                "(SELECT COUNT (*) FROM reviews_likes WHERE review_id = ? AND is_like) - " +
                "(SELECT COUNT (*) FROM reviews_likes WHERE review_id = ? AND NOT is_like)";
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