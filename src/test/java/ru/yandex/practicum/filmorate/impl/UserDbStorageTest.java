package ru.yandex.practicum.filmorate.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDbStorageTest {

    private final UserService userService;

    @Test
    public void testFindUserById() {

        User user = new User("g@mail.ru", "new login", "new name", LocalDate.of(1999, 8, 23));
        userService.addUser(user);
        user.setId(1);

        assertEquals(user, userService.getUserById(1));
    }

    @Test
    public void testAddUser() {
        User user = new User("g@mail.ru", "new login", "new name", LocalDate.of(1999, 8, 23));
        User indexUser = userService.addUser(user);
        System.out.println(indexUser.getId());
        assertEquals(1, userService.getAllUsers().size());
    }

    @Test
    public void testGetAllUsers() {
        User user = new User("g@mail.ru", "new login", "new name",
                LocalDate.of(1999, 8, 23));
        User user2 = new User("another", "another", "another",
                LocalDate.of(1999, 8, 23));
        userService.addUser(user);
        userService.addUser(user2);
        assertEquals(2, userService.getAllUsers().size());
    }

    @Test
    public void testChangeUser() {
        User user = new User("g@mail.ru", "login", "THE",
                LocalDate.of(1999, 8, 23));
        User newUser = new User("new@mail.ru", "new login", "new name",
                LocalDate.of(1999, 8, 23));
        long index = userService.addUser(user).getId();

        newUser.setId(index);
        assertEquals(newUser, userService.changeUser(newUser));
    }

    @Test
    public void testAddFriendToUser() {
        User one = new User("g@mail.ru", "login", "one",
                LocalDate.of(1999, 8, 23));
        User two = new User("new@mail.ru", "new login", "two",
                LocalDate.of(1999, 8, 23));

        User indexOne = userService.addUser(one);
        User indexTwo = userService.addUser(two);
        userService.addFriendToUser(indexOne.getId(), indexTwo.getId());
        assertEquals(1, userService.getUserFriends(indexOne.getId()).size());
    }

    @Test
    @Order(7)
    public void testGetUserFriends() {
        User one = new User("g@mail.ru", "login", "one",
                LocalDate.of(1999, 8, 23));
        User two = new User("new@mail.ru", "new login", "two",
                LocalDate.of(1999, 8, 23));
        User three = new User("new@mail.ru", "new login", "three",
                LocalDate.of(1999, 8, 23));
        User indexOne = userService.addUser(one);
        User indexTwo = userService.addUser(two);
        User indexThree = userService.addUser(three);

        userService.addFriendToUser(indexOne.getId(), indexTwo.getId());
        userService.addFriendToUser(indexOne.getId(), indexThree.getId());

        List<User> friends = new ArrayList<>(Arrays.asList(indexTwo, indexThree));
        assertEquals(friends, userService.getUserFriends(indexOne.getId()));
    }

    @Test
    public void testDeleteFromFriend() {
        User one = new User("g@mail.ru", "login", "one",
                LocalDate.of(1999, 8, 23));
        User two = new User("new@mail.ru", "new login", "two",
                LocalDate.of(1999, 8, 23));
        User indexOne = userService.addUser(one);
        User indexTwo = userService.addUser(two);
        userService.addFriendToUser(indexOne.getId(), indexTwo.getId());
        userService.deleteFromFriend(indexOne.getId(), indexTwo.getId());
        assertEquals(0, userService.getUserFriends(indexOne.getId()).size());
    }
}