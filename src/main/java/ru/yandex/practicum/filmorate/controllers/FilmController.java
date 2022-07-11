package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
//@RequestMapping("/films")
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
        log.info(String.format("Film %s was added", film.getName()));
        return filmService.addFilm(film);
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable long id) {
        log.info(String.format("Get film %d", id));
        return filmService.getFilmById(id);
    }

    @PutMapping("/films")
    public Film changeFilm(@Valid @RequestBody Film film) throws ValidationException {
        log.info(String.format("Film %d was changed", film.getId()));
        return filmService.changeFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void like(@PathVariable long id, @PathVariable long userId) {
        log.info(String.format("User %d likes film %d", userId, id));
        filmService.like(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info(String.format("User %d deleted like from film %d", userId, id));
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Get popular films");
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/mpa/{id}")
    public MPA getMpaById(@PathVariable int id) {
        log.info(String.format("Get MPA %d", id));
        return filmService.getMpaById(id);
    }

    @GetMapping("/mpa")
    public List<MPA> getAllMpa() {
        log.info("Get all MPA");
        return filmService.getAllMpa();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable int id) {
        log.info(String.format("Get genre %d", id));
        return filmService.getGenreById(id);
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        log.info("Get all genres");
        return filmService.getAllGenres();
    }
}
