package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Friends.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendsDbStorageTests {
    User user1;
    User user2;
    User user3;
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
        user3 = new User(3, "testuser3@example.com", "testUser3",
                "TestUser2", LocalDate.of(2003, 3, 3));
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user3);
    }

    @Test
    public void addToFriendsTest() {
        long userId = user1.getId();
        long friendId = user2.getId();

        friendsStorage.addToFriends(userId, friendId);

        SqlRowSet result = jdbcTemplate.queryForRowSet("SELECT * FROM friendships WHERE user_id = ? AND friend_id = ?", userId, friendId);
        assertTrue(result.next());
    }

    @Test
    public void deleteFromFriendTest() {
        long userId = user1.getId();
        long friendId = user2.getId();

        friendsStorage.addToFriends(userId, friendId);

        friendsStorage.deleteFromFriend(userId, friendId);

        SqlRowSet result = jdbcTemplate.queryForRowSet("SELECT * FROM friendships WHERE user_id = ? AND friend_id = ?", userId, friendId);
        assertFalse(result.next());
    }

    @Test
    public void isAlreadyFriendTest() {
        long userId = user1.getId();
        long friendId = user2.getId();

        assertFalse(friendsStorage.isAlreadyFriend(userId, friendId));

        jdbcTemplate.update("insert into friendships (user_id, friend_id) values (?, ?)", userId, friendId);

        assertTrue(friendsStorage.isAlreadyFriend(userId, friendId));
    }

    @Test
    public void getAllFriendsTest() {
        long userId = user1.getId();
        long friendId = user2.getId();

        friendsStorage.addToFriends(userId, friendId);

        List<User> friends = friendsStorage.getAllFriends(userId);
        assertEquals(1, friends.size());
        assertEquals(user2, friends.get(0));
    }

    @Test
    public void friendsOfBothUsersTest() {
        friendsStorage.addToFriends(user1.getId(), user3.getId());
        friendsStorage.addToFriends(user2.getId(), user3.getId());

        List<User> friendsOfBoth = friendsStorage.friendsOfBothUsers(user1.getId(), user2.getId());
        assertEquals(friendsOfBoth.size(), 1);
        assertEquals(friendsOfBoth.get(0), user3);
    }

}
