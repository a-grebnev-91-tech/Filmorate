package ru.yandex.practicum.filmorate.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.impl.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmDbStorageTest {

    private final FilmService filmService;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    private final LikesDbStorage likesDbStorage;

    private final MpaDbStorage mpaDbStorage;

    private final GenreDbStorage genreDbStorage;

    @Test
    public void testAddFilm() {
        Film film = new Film("film", "description", LocalDate.of(2022, 7, 25),
                90, 4);
        film.setMpa(new Mpa(1, "G"));
        film.setId(1);
        filmDbStorage.addFilm(film);

        assertEquals(1, filmDbStorage.getFilms().size());
    }

    @Test
    public void testGetFilmById() {
        Film film = new Film("film", "description", LocalDate.of(2022, 7, 25),
                90, 4);
        film.setMpa(new Mpa(1, "G"));
        film.setId(1);
        filmService.addFilm(film);

        assertEquals(film, filmService.getFilmById(1));
    }

    @Test
    public void testChangeFilm() {
        Film film = new Film("film", "description", LocalDate.of(2022, 7, 25),
                90, 4);
        film.setMpa(new Mpa(1, "G"));
        Film newFilm = new Film("NEW", "description", LocalDate.of(2022, 7, 25),
                90, 4);
        newFilm.setMpa(new Mpa(1, "G"));
        newFilm.setId(1);
        filmService.addFilm(film);
        filmService.changeFilm(newFilm);

        assertEquals(newFilm, filmService.getFilmById(1));
    }

    @Test
    public void testChangeFilmWithFilmNotFoundException() {
        Film film = new Film("film", "description", LocalDate.of(2022, 7, 25),
                90, 4);
        film.setMpa(new Mpa(1, "G"));
        film.setId(1);
        Film wrongFilm = new Film("wrong film", "wrong description", LocalDate.of(2022, 7, 25),
                90, 4);
        wrongFilm.setMpa(new Mpa(1, "G"));
        wrongFilm.setId(2);
        filmDbStorage.addFilm(film);

        ModelNotFoundException ex = assertThrows(ModelNotFoundException.class, () -> filmDbStorage.changeFilm(wrongFilm));

        assertEquals("Film not found with id " + wrongFilm.getId(), ex.getMessage());
    }

    @Test
    public void testLike() {
        Film film = new Film("film", "description", LocalDate.of(2022, 7, 25),
                90, 4);
        film.setMpa(new Mpa(1, "G"));
        film.setId(1);

        User user = new User(1, "email", "login", "name", LocalDate.of(2022, 8, 15));
        userDbStorage.addUser(user);

        filmService.addFilm(film);
        likesDbStorage.like(1, 1);

        assertEquals(1, filmService.getFilmById(1).getLikes().size());
    }

    @Test
    public void testDeleteLike() {
        Film film = new Film("film", "description", LocalDate.of(2022, 7, 25),
                90, 4);
        film.setMpa(new Mpa(1, "G"));
        film.setId(1);

        User user = new User(1, "email", "login", "name", LocalDate.of(2022, 8, 15));
        userDbStorage.addUser(user);

        filmDbStorage.addFilm(film);
        likesDbStorage.like(1, 1);
        likesDbStorage.deleteLike(1, 1);

        assertEquals(0, filmDbStorage.getFilmById(1).getLikes().size());
    }

    @Test
    public void testGetFilmByIdWithFilmNotFoundException() {
        ModelNotFoundException ex = assertThrows(ModelNotFoundException.class, () -> filmDbStorage.getFilmById(1));

        assertEquals("Film wasn't found", ex.getMessage());
    }

    @Test
    public void testGetMpaById() {
        assertEquals("G", mpaDbStorage.getMpaById(1).getName());
    }

    @Test
    public void testGetAllMpa() {
        List<Mpa> mpa = new ArrayList<>();
        mpa.add(new Mpa(1, "G"));
        mpa.add(new Mpa(2, "PG"));
        mpa.add(new Mpa(3, "PG-13"));
        mpa.add(new Mpa(4, "R"));
        mpa.add(new Mpa(5, "NC-17"));

        assertEquals(mpa, mpaDbStorage.getAllMpa());
    }

    @Test
    public void testGetGenreById() {
        assertEquals("Комедия", genreDbStorage.getGenreById(1).getName());
    }

    @Test
    public void testGetAllGenres() {
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(2, "Драма"));
        genres.add(new Genre(3, "Мультфильм"));
        genres.add(new Genre(4, "Ужасы"));
        genres.add(new Genre(5, "Триллер"));
        genres.add(new Genre(6, "Детектив"));

        assertEquals(genres, genreDbStorage.getAllGenres());
    }
}