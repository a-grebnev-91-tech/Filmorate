package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info("Film {} was added", film.getName());
        return filmService.addFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        log.info("User {} likes film {}", userId, filmId);
        filmService.addLike(filmId, userId);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Get all films");
        return filmService.getAllFilms();
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        log.info("Get popular films shared with a friend.");
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable long directorId,
                                         @RequestParam String sortBy) {
        log.info("Get sorted films by director with id {}", directorId);
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        log.info("Get film {}", id);
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") int count,
                                  @RequestParam Optional<Long> genreId,
                                  @RequestParam Optional<Long> year) {
        log.info("Get list of the most popular films by genre and(or) year.");
        return filmService.getPopularFilmsByGenreAndYear(count, genreId, year);
    }

    @DeleteMapping("{filmId}/directors/{directorId}")
    public void removeDirectorFromFilm(@PathVariable long filmId, @PathVariable long directorId) {
        log.info("Delete director {} from film {}", directorId, filmId);
        filmService.removeDirectorFromFilm(filmId, directorId);
    }

    @DeleteMapping("/{id}")
    public void removeFilm(@PathVariable long id) {
        log.info("Delete film {}", id);
        filmService.deleteFilm(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        log.info("User {} deleted like from film {}", userId, filmId);
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/search")
    public List<Film> searchFilm(@RequestParam String query,
                                 @RequestParam List<String> by) {
        log.info("Searching substring '{}' at films {}", query, by);
        return filmService.searchFilm(query, by);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("Film {} was updated", film.getId());
        return filmService.changeFilm(film);
    }
}