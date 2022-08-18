package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EventsStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventsStorage eventsStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, UserStorage userStorage,
                         FilmStorage filmStorage, EventsStorage eventsStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventsStorage = eventsStorage;
    }

    public Review addReview(Review review) {
        if (isFilmExist(review.getFilmId()) && isExistUser(review.getUserId())) {
            long id = reviewStorage.createReview(review);
            review.setReviewId(id);
            Event event = new Event(review.getUserId(), id,
                    EventType.REVIEW,
                    EventOperations.ADD);
            eventsStorage.addEvent(event);
            return review;
        } else {
            throw new NotFoundException("Model not found");
        }
    }

    public Review changeReview(Review review) {
        reviewStorage.updateReview(review);
        Review result = reviewStorage.getReview(review.getReviewId());
        Event event = new Event(result.getUserId(), review.getReviewId(),
                EventType.REVIEW,
                EventOperations.UPDATE);
        eventsStorage.addEvent(event);
        return review;
    }

    public void deleteReview(long id) {
        Review review = getReviewById(id);
        Event event = new Event(review.getUserId(), review.getReviewId(),
                EventType.REVIEW,
                EventOperations.REMOVE);
        eventsStorage.addEvent(event);
        reviewStorage.deleteReview(id);
    }

    public Review getReviewById(long id) {
        try {
            return reviewStorage.getReview(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException(String.format("Review with id %d isn't exist", id));
        }
    }

    public List<Review> getReviewByFilmId(Optional<Long> filmId, int count) {
        List<Review> allReviews;
        if (filmId.isPresent()) {
            allReviews = reviewStorage.getFilmsReviews(filmId.get(), count);
        } else {
            allReviews = reviewStorage.getSomeReviews(count);
        }
        allReviews.sort((o1, o2) -> Integer.compare(o2.getUseful(), o1.getUseful()));
        return allReviews;
    }

    public void addLike(long id, long userId, boolean islike) {
        reviewStorage.addLike(id, userId, islike);
    }

    public void deleteLike(long id, long userId, boolean isLike) {
        reviewStorage.deleteLike(id, userId, isLike);
    }

    private boolean isExistUser(long id) {
        Optional<User> user = Optional.ofNullable(userStorage.getUser(id));
        return user.isPresent();
    }

    private boolean isFilmExist(long id) {
        try {
            filmStorage.getFilmById(id);
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
        return true;
    }
}