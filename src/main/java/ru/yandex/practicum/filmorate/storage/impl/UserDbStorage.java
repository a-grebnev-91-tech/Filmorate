package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component("userDBStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //todo
    @Override
    public User createUser(User user) {
        List<User> users = getAllUsers();
        if (users.contains(user)) {
            throw new ModelAlreadyExistException("User already exist");
        } else {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("users")
                    .usingGeneratedKeyColumns("user_id");
            long id = insert.executeAndReturnKey(user.toMap()).longValue();
            user.setId(id);
            return user;
        }
    }

    @Override
    public boolean deleteUser(long id) {
        String sqlQuery =
                "DELETE FROM users " +
                        "WHERE user_id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getUser(long id) {
        try {
            String sqlQuery =
                    "SELECT * FROM users " +
                            "WHERE user_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ModelNotFoundException("User wasn't found");
        }
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery =
                "UPDATE users " +
                        "SET " +
                        "email = ?, " +
                        "login = ?, " +
                        "name = ?, " +
                        "birthdate = ? " +
                        "WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return getUser(user.getId());
    }
    //TODO move to frinds STORAGE

    @Override
    public List<User> getUserFriends(long id) {
        String sqlQuery =
                "SELECT * FROM users " +
                        "WHERE user_id IN " +
                        "(SELECT friend_id " +
                        "FROM friends " +
                        "WHERE user_id = ?)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
    }

    //TODO del this
    public boolean deleteAllUsers() {
        String sql = "DELETE FROM users";
        return jdbcTemplate.update(sql) > 0;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("user_id");
        String email = resultSet.getString("email");
        String login = resultSet.getString("login");
        String name = resultSet.getString("name");
        LocalDate birthdate = resultSet.getDate("birthdate").toLocalDate();
        return new User(id, email, login, name, birthdate);
    }
}