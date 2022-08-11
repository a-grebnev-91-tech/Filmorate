package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.validators.DirectorValidate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DirectorService {
    private final DirectorStorage directorStorage;
    private final DirectorValidate directorValidate;

    @Autowired
    public DirectorService(DirectorStorage directorStorage, DirectorValidate directorValidate) {
        this.directorStorage = directorStorage;
        this.directorValidate = directorValidate;
    }

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director getDirector(Long directorId) {
        directorValidate.validateIdDirector(directorId);
        Optional<Director> director = directorStorage.getDirector(directorId);
        if (director.isPresent()) {
            return director.get();
        } else {
            throw new ModelNotFoundException(String.format("Not found director %s", directorId));
        }
    }

    public Director addDirector(Director director) {
        directorValidate.validateNameAndExist(director);
        return directorStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        directorValidate.validateNameAndId(director);
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(Long directorId) {
        directorValidate.validateIdDirector(directorId);
        directorStorage.deleteDirector(directorId);
    }

    public void deleteDirectorFromFilm(Long filmId, Long directorId) {
        directorValidate.validateIdDirector(directorId);
        directorValidate.validateFilmId(filmId);
        directorStorage.deleteDirectorFromFilm(filmId, directorId);
    }

    public void addDirectorToFilm(Film film, Long directorId) {
        directorValidate.validateIdDirector(directorId);
        directorStorage.addDirectorToFilm(film, directorId);
    }

    public void updateDirectorToFilm(Film film) {
        Set<Director> directors = film.getDirectors();
        if (directors == null || directors.isEmpty()) {
            film.setDirectors(null);
            directorStorage.deleteAllDirectorsFromFilm(film.getId());
        } else {
            for (Director d : directors) {
                directorStorage.updateDirectorToFilm(film.getId(), d.getId());
            }
        }
    }

    public List<Director> getDirectorsByFilm(Long filmId) {
        directorValidate.validateFilmId(filmId);
        return directorStorage.getDirectorsByFilm(filmId);
    }
}