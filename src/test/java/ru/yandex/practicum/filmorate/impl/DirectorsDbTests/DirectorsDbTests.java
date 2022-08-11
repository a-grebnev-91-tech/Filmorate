package ru.yandex.practicum.filmorate.impl.DirectorsDbTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DirectorsDbTests {
    private final FilmService filmService;
    private final UserService userService;
    private final DirectorService directorService;

    @Test
    public void checkAddDirectors() {
        assertEquals(0, directorService.getAllDirectors().size());
        Director director = new Director(1, "dir1");
        Director director2 = new Director(2, "dir1");
        Director director3 = new Director(3, "dir1");
        Director director4 = new Director("dir1");
        Director director5 = new Director(10, "dir1");
        directorService.addDirector(director);
        directorService.addDirector(director2);
        directorService.addDirector(director3);
        directorService.addDirector(director4);
        directorService.addDirector(director5);
        assertEquals(5, directorService.getAllDirectors().size());
        Director director6 = new Director(10, null);
        ValidationException ex = assertThrows(ValidationException.class, () -> directorService.addDirector(director6));
        assertEquals("имя директора null", ex.getMessage());
        Director director7 = new Director(1, "");
        ValidationException ex2 = assertThrows(ValidationException.class, () -> directorService.addDirector(director7));
        assertEquals("имя директора пустое", ex2.getMessage());
        Director director8 = new Director(1, "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq" +
                "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
        ValidationException ex3 = assertThrows(ValidationException.class, () -> directorService.addDirector(director8));
        assertEquals("имя директора длиннее 50 символов", ex3.getMessage());
    }

    @Test
    public void checkAddDAndDeleteDirectorsInFilm() {
        Director director = new Director(1, "dir1");
        Director director2 = new Director(2, "dir1");
        Director director3 = new Director(3, "dir1");
        directorService.addDirector(director);
        directorService.addDirector(director2);
        directorService.addDirector(director3);
        Film film = new Film("film", "description", LocalDate.of(2022, 7, 25),
                90, 4);
        film.setMpa(new Mpa(1, "G"));
        film.setId(1);
        filmService.addFilm(film);
        assertEquals(0, film.getDirectors().size());
        film.addDirector(director);
        film.addDirector(director2);
        film.addDirector(director3);
        filmService.changeFilm(film);
        directorService.getDirectorsByFilm(film.getId());
        assertEquals(3, directorService.getDirectorsByFilm(film.getId()).size());
        directorService.deleteDirectorFromFilm(film.getId(), director.getId());
        directorService.deleteDirectorFromFilm(film.getId(), director2.getId());
        assertEquals(1, directorService.getDirectorsByFilm(film.getId()).size());
        directorService.getDirector(director.getId());
    }

    @Test
    public void checkSortFilmOfDirector() {
        Director director = new Director(1, "dir1");
        Director director2 = new Director(2, "dir1");
        Director director3 = new Director(3, "dir1");
        directorService.addDirector(director);
        directorService.addDirector(director2);
        directorService.addDirector(director3);

        Film film = new Film("film", "description", LocalDate.of(2022, 7, 25),
                90, 4);
        film.setMpa(new Mpa(1, "G"));
        film.setId(1);
        Film film2 = new Film("film2", "description2", LocalDate.of(2012, 7, 25),
                90, 4);
        film2.setMpa(new Mpa(2, "G"));
        film2.setId(2);

        User user = new User("g@mail.ru", "new login", "user", LocalDate.of(1999, 8, 23));
        user.setId(1);
        User user2 = new User("g2@mail.ru", "new login2", "user2", LocalDate.of(1999, 8, 23));
        user.setId(2);
        User user3 = new User("g3@mail.ru", "new login3", "user3", LocalDate.of(1999, 8, 23));
        user.setId(3);
        User user4 = new User("g4@mail.ru", "new login4", "user4", LocalDate.of(1999, 8, 23));
        user.setId(4);
        userService.addUser(user);
        userService.addUser(user2);
        userService.addUser(user3);
        userService.addUser(user4);
        filmService.addFilm(film);
        filmService.addFilm(film2);

        film.addDirector(director);
        film2.addDirector(director);
        filmService.like(film.getId(), user.getId());
    }
}