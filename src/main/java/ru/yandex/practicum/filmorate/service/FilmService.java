package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final LikesStorage likesStorage;
    private final DirectorService directorsStorage;
    private final EventsStorage eventsStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage,
                       UserStorage userStorage,
                       EventsStorage eventsStorage,
                       GenreStorage genreStorage,
                       LikesStorage likesStorage,
                       DirectorService directorsStorage) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
        this.likesStorage = likesStorage;
        this.directorsStorage = directorsStorage;
        this.eventsStorage = eventsStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        film.setRate(0);
        long idFilm = filmStorage.createFilm(film);
        genreStorage.addGenresToFilm(film, idFilm);
        film.setId(idFilm);
        addDirectorInFilm(film);
        return film;
    }

    public Film getFilmById(long id) {
        Film film;
        try {
            film = filmStorage.getFilmById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Film wasn't found");
        }
        film.setLikes(likesStorage.getLikes(id));
        film.setGenres(getGenresByFilmId(id));
        film.setDirectors(new HashSet<>(directorsStorage.getDirectorsByFilm(id)));
        return film;
    }

    private Set<Genre> getGenresByFilmId(long filmId) {
        return new HashSet<>(genreStorage.getFilmGenres(filmId));
    }

    public Film changeFilm(Film film) {
        try {
            filmStorage.getFilmById(film.getId());
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Film wasn't found");
        }
        filmStorage.updateFilm(film);
        genreStorage.changeFilmGenres(film);
        directorsStorage.updateDirectorToFilm(film);
        return film;
    }

    public void addLike(long filmId, long userId) {
        likesStorage.addLike(filmId, userId);
        likesStorage.updateRate(filmId);
        Event event = new Event(userId, filmId, EventType.LIKE, EventOperations.ADD);
        eventsStorage.addEvent(event);
    }

    public void deleteLike(long filmId, long userId) {
        Film film = getFilmById(filmId);
        if (film.getLikes().contains(userId)) {
            likesStorage.removeLike(filmId, userId);
            likesStorage.updateRate(filmId);
            Event event = new Event(userId, filmId, EventType.LIKE, EventOperations.REMOVE);
            eventsStorage.addEvent(event);
        } else {
            throw new NotFoundException("User not found with id " + userId);
        }
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        films.forEach(this::constructFilm);
        return films;
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = likesStorage.getPopularFilms(count);
        films.forEach(this::constructFilm);
        return films;
    }

    public void deleteFilm(long id) {
        getFilmById(id);
        filmStorage.deleteFilm(id);
    }

    public void removeDirectorFromFilm(long filmId, long directorId) {
        log.info("Start filmService. Method deleteDirectorInFilm. directorId:{},  filmId{}.", directorId, filmId);
        directorsStorage.deleteDirectorFromFilm(filmId, directorId);
    }

    public void addDirectorInFilm(Film film) {
        log.info("Start filmService. Method addDirectorInFilm. film:{}.", film);
        List<Director> directors = new ArrayList<>(film.getDirectors());
        for (Director director : directors) {
            directorsStorage.addDirectorToFilm(film, director.getId());
        }
    }

    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        log.info("Start filmService. Method getSortFilmByDirector. directorId:{}, parameter:{}.", directorId, sortBy);
        directorsStorage.getDirector(directorId);
        List<Film> films;
        switch (sortBy) {
            case "year":
                films = filmStorage.getFilmsByDirectorSortedByYear(directorId);
                break;
            case "likes":
                films = filmStorage.getFilmsByDirectorSortedByLikes(directorId);
                break;
            default:
                throw new NotFoundException("Wrong sort");
        }
        films.forEach(this::constructFilm);
        return films;
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        List<Film> films = filmStorage.getPopularFilmsSharedWithFriend(userId, friendId);
        films.forEach(this::constructFilm);
        return films;
    }

    public List<Film> getPopularFilmsByGenreAndYear(int limit, Optional<Long> genreId, Optional<Long> year) {
        List<Film> films;
        if (genreId.isEmpty() && year.isEmpty()) {
            films = getPopularFilms(limit);
        } else if (genreId.isPresent() && year.isEmpty()) {
            films = filmStorage.getPopularFilmsByGenre(limit, genreId.get());
        } else if (genreId.isEmpty()) {
            films = filmStorage.getPopularFilmsByYear(limit, year.get());
        } else {
            films = filmStorage.getPopularFilmsByGenreAndYear(limit, genreId.get(), year.get());
        }
        films.forEach(this::constructFilm);
        return films;
    }

    public List<Film> getRecommendations(long userId) {
        userStorage.getUser(userId);
        List<Long> recommendationsIds = likesStorage.getRecommendations(userId);
        List<Film> recommendations = filmStorage.getSomeFilms(recommendationsIds);
        recommendations.forEach(this::constructFilm);
        return recommendations;
    }

    public List<Film> searchFilm(String query, List<String> by) {
        Set<Film> films = new TreeSet<>(Comparator.comparing(Film::getRate).reversed().thenComparing(Film::getId));
        for (String sortBy : by) {
            switch (sortBy) {
                case "title":
                    films.addAll(filmStorage.searchByTitles(query));
                    break;
                case "director":
                    films.addAll(filmStorage.searchByDirectors(query));
                    break;
                default:
                    throw new ValidationException("Bad search argument");
            }
        }
        films.forEach(this::constructFilm);
        return new ArrayList<>(films);
    }

    private void constructFilm(Film film) {
        film.setGenres(getGenresByFilmId(film.getId()));
        film.setLikes(likesStorage.getLikes(film.getId()));
        film.setDirectors(new HashSet<>(directorsStorage.getDirectorsByFilm(film.getId())));
    }
}