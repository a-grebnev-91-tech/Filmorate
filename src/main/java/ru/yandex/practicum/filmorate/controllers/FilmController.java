package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@Validated
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Get all films");
        return filmService.getFilms();
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (filmService.checkDate(film)) {
            log.info("Film {} was added", film.getName());
            return filmService.addFilm(film);
        } else {
            throw new ValidationException("Wrong release date");
        }
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable long id) {
        log.info("Get film {}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/films")
    public Film changeFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (filmService.checkDate(film)) {
            log.info("Film {} was changed", film.getId());
            return filmService.changeFilm(film);
        } else {
            throw new ValidationException("Wrong release date");
        }
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void like(@PathVariable long id, @PathVariable long userId) {
        log.info("User {} likes film {}", userId, id);
        filmService.like(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info("User {} deleted like from film {}", userId, id);
        filmService.deleteLike(id, userId);
    }

    @DeleteMapping("/films/{id}")
    public void deleteFilmById(@PathVariable long id) {
        log.info("Delete film {}", id);
        filmService.deleteFilm(id);
    }


    @GetMapping("/films/director/{directorId}")
    public List<Film> getSortFilmByDirector(@PathVariable long directorId,
                                            @RequestParam String sortBy) {
        log.info("Get sorted films by director with id {}", directorId);
        return filmService.getSortedFilmsByDirector(directorId, sortBy);
    }

    @DeleteMapping("/films{filmId}/directors/{directorId}")
    public void deleteDirectorsFromFilm(@PathVariable long filmId, @PathVariable long directorId) {
        log.info("Delete director {} from film {}", directorId, filmId);
        filmService.deleteDirectorInFilm(filmId, directorId);
    }

    @GetMapping("/films/common")
    public List<Film> getPopularFilmsSharedWithFriend(@RequestParam long userId, @RequestParam long friendId) {
        log.info("Get popular films shared with a friend.");
        return filmService.getPopularFilmsSharedWithFriend(userId, friendId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilmsByGenreAndYear(@RequestParam(defaultValue = "10") int count,
                                                    @RequestParam Optional<Long> genreId,
                                                    @RequestParam Optional<Long> year){
        log.info("Get list of the most popular films by genre and(or) year.");
        return filmService.getPopularFilmsByGenreAndYear(count, genreId, year);
    }

    @GetMapping("/films/search")
    public List<Film> search(@RequestParam String query,
                             @RequestParam List<String> by) {
        log.info("Searching substring '{}' at films {}", query, by);
        return filmService.searchFilm(query, by);
    }
}