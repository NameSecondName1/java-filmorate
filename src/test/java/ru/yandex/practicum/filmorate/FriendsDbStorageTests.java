package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Friends.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendsDbStorageTests {
    User user1;
    User user2;
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    UserDbStorage userStorage;
    @Autowired
    FriendsDbStorage friendsStorage;

    @BeforeEach
    public void beforeEach() {
        jdbcTemplate.execute("delete from friendships");
        jdbcTemplate.execute("delete from users");
        user1 = new User(1, "testuser1@example.com", "testUser1",
                "TestUser1", LocalDate.of(2001, 1, 1));

        user2 = new User(2, "testuser2@example.com", "testUser2",
                "TestUser2", LocalDate.of(2002, 2, 2));
        userStorage.create(user1);
        userStorage.create(user2);
    }

    @Test
    public void isAlreadyFriendTest() {
        long userId = user1.getId();
        long friendId = user2.getId();
        assertFalse(friendsStorage.isAlreadyFriend(userId, friendId));
        jdbcTemplate.update("insert into friendships (user_id, friend_id) values (?, ?)", userId, friendId);
        assertTrue(friendsStorage.isAlreadyFriend(userId, friendId));
    }

}
