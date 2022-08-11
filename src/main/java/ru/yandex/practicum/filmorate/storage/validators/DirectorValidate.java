package ru.yandex.practicum.filmorate.storage.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

@Slf4j
@Component
public class DirectorValidate {
    private final JdbcTemplate jdbcTemplate;
    private static final String CHECK_ID_DIR = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
    private static final String CHECK_DIR = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ? and DIRECTOR_NAME = ?";
    private static final String CHECK_ID_FILM = "SELECT * FROM FILM WHERE FILM_ID = ?";

    @Autowired
    public DirectorValidate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void validateIdDirector(Long directorId) {
        log.info("Check director id {} ", directorId);
        SqlRowSet sqlRow = jdbcTemplate.queryForRowSet(CHECK_ID_DIR, directorId);
        if (!sqlRow.next()) {
            log.error("Director not found {}", directorId);
            throw new ModelNotFoundException("Director not found");
        }
    }

    private void validateExistFilm(Director director) {
        SqlRowSet sqlRow = jdbcTemplate.queryForRowSet(CHECK_DIR, director.getId(), director.getName());
        if (sqlRow.next()) {
            log.error("Director already exist {}", director);
            throw new ModelAlreadyExistException("Director already exist");
        }
    }

    private void validateNameDirector(Director director) {
        if (director.getName() == null) {
            log.warn("Name shouldn't be null");
            throw new ValidationException("Name shouldn't be null");
        }
        if (director.getName().isEmpty() || director.getName().equals(" ")) {
            log.warn("Name shouldn't be empty");
            throw new ValidationException("Name shouldn't be empty");
        }
        if (director.getName().length() > 50) {
            log.warn("Name should be less than 50 characters");
            throw new ValidationException("Name should be less than 50 characters");
        }
    }

    public void validateNameAndExist(Director director) {
        validateExistFilm(director);
        validateNameDirector(director);
    }

    public void validateNameAndId(Director director) {
        validateIdDirector(director.getId());
        validateNameDirector(director);
    }

    public void validateFilmId(Long filmId) {
        if (filmId < 0) {
            throw new ModelNotFoundException("filmId less than 0");
        }
        SqlRowSet sqlRow = jdbcTemplate.queryForRowSet(CHECK_ID_FILM, filmId);
        if (!sqlRow.next()) {
            throw new ValidationException("Film not found");
        }
    }
}