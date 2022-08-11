package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User addUser(User user);

    User changeUser(User user);

    User findUserById(long id);

    List<User> getUserFriends(long id);

    boolean deleteUser(long id);
}