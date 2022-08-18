package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    long createReview(Review review);

    void updateReview(Review review);

    void deleteReview(long id);

    Review getReview(long id);

    void addLike(long id, long userId, boolean isLike);

    void deleteLike(long id, long userId, boolean isLike);

    List<Review> getFilmsReviews(long filmId, int count);

    List<Review> getSomeReviews(int count);

    List<Review> getAllReviews();
}