package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    long addReview(Review review);

    void changeReview(Review review);

    void deleteReview(long id);

    Review getReviewById(long id);

    void addLike(long id, long userId, boolean isLike);

    void deleteLike(long id, long userId, boolean isLike);

    List<Review> getReviewByFilmId(long filmId, int count);

    List<Review> getCountReview(int count);

    List<Review> getAllReview();
}