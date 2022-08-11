package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriendToUser(long userId, long friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id)" +
                "VALUES(?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void deleteFromFriends(long userId, long friendId) {
        String sql = "DELETE FROM friends " +
                "WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<Long> getUserFriends(long id) {
        String sql = "SELECT friend_id FROM friends " +
                "WHERE user_id = ?";
        return jdbcTemplate.query(sql, this::rowMapToLongIdFriends, id);
    }

    private long rowMapToLongIdFriends(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("friend_id");
    }
}