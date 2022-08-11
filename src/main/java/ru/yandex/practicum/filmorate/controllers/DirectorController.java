package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
public class DirectorController {

    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping("/directors")
    public List<Director> getAllDirectors() {
        log.info("Get all directors");
        return directorService.getAllDirectors();
    }

    @PostMapping("/directors")
    public Director addDirector(@RequestBody Director director) {
        log.info("Add director {}", director.getName());
        return directorService.addDirector(director);
    }

    @GetMapping("/directors/{id}")
    public Director getDirector(@PathVariable("id") Long directorId) {
        log.info("Get director {}", directorId);
        return directorService.getDirector(directorId);
    }

    @PutMapping("/directors")
    public Director updateDirector(@RequestBody Director director) {
        log.info("Update director {}", director.getId());
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    public void deleteDirector(@PathVariable("id") Long directorId) {
        log.info("Delete director {}", directorId);
        directorService.deleteDirector(directorId);
    }
}