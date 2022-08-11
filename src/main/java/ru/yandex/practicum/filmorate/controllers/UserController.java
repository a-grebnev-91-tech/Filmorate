package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {
    private final UserService userService;
    private final FilmService filmService;

    @Autowired
    public UserController(UserService userService, FilmService filmService) {
        this.userService = userService;
        this.filmService = filmService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Returned all users");
        return userService.getAllUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        User result = userService.addUser(userService.checkName(user));
        log.info("User was added {}", result.getName());
        return result;
    }

    @PutMapping
    public User changeUser(@Valid @RequestBody User user) {
        log.info("User was changed {}", user.getId());
        return userService.changeUser(userService.checkName(user));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addToFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("User {} was added like friend to user {}", friendId, id);
        userService.addFriendToUser(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteUserFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("User {} was deleted from {}", friendId, id);
        userService.deleteFromFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable long id) {
        log.info("Get all the user's {} friends", id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Get common friends between user {} and user {}", id, otherId);
        return userService.getCommonFriend(id, otherId);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        log.info("Get user {}", id);
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable long id) {
        log.info("Get recommendations for user with id {}", id);
        return filmService.getRecommendations(id);
    }
    
    @GetMapping("/{id}/feed")
    public List<Event> getEvents(@PathVariable("id") Long id) {
        log.info("Get events {}", id);
        return userService.getEvents(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") long id) {
        log.info("Delete user {}", id);
        userService.deleteUser(id);
    }
}