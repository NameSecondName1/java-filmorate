package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.UsersAlreadyFriendsException;
import ru.yandex.practicum.filmorate.exception.UsersNotFriendsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.Friends.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTests {
    User user1;
    User user2;
    User user3;
    UserController userController;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    UserRowMapper userRowMapper;

    @BeforeEach
    public void BeforeEach() {
        userController = new UserController(
                new UserService(
                        new UserDbStorage(jdbcTemplate,userRowMapper),
                        new FriendsDbStorage(jdbcTemplate, userRowMapper))
        );

        jdbcTemplate.execute("delete from friendships");
        jdbcTemplate.execute("DELETE FROM users");

        user1 = new User(1, "testuser1@example.com", "testUser1",
                "TestUser1", LocalDate.of(2000, 1, 1));

        user2 = new User(2, "testuser2@example.com", "testUser2",
                "TestUser2", LocalDate.of(2000, 2, 2));
        user3 = new User(3, "testuser3@example.com", "testUser3",
                "TestUser2", LocalDate.of(2003, 3, 3));

    }
    @Test
    public void testGetAllUsers() {
        userController.create(user1);
        userController.create(user2);

        List<User> allUsers = userController.getAllUsers();

        assertThat(allUsers).containsExactlyInAnyOrder(user1, user2);
    }
    @Test
    public void testGetUserById() {
        userController.create(user1);

        Optional<User> userOptional = Optional.ofNullable(userController.getUserById(user1.getId()));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", user1.getId())
                );
    }
    @Test
    public void testCreateUser() {

        User createdUser = userController.create(user1);

        assertThat(createdUser.getId()).isPositive();

        String sql = "SELECT * FROM users WHERE id = ?";
        User retrievedUser = jdbcTemplate.queryForObject(sql, new Object[]{createdUser.getId()}, new UserRowMapper());

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getName()).isEqualTo(user1.getName());
        assertThat(retrievedUser.getLogin()).isEqualTo(user1.getLogin());
        assertThat(retrievedUser.getEmail()).isEqualTo(user1.getEmail());
        assertThat(retrievedUser.getBirthday()).isEqualTo(user1.getBirthday());
    }
    @Test
    public void testUpdateUser() {
        userController.create(user1);

        user1.setName("UpdatedTestUser1");
        user1.setLogin("updatedTestUser1");
        user1.setEmail("updatedTestUser1@example.com");
        user1.setBirthday(LocalDate.of(2000, 2, 2));
        userController.update(user1);

        User updatedUser = userController.getUserById(user1.getId());

        assertThat(updatedUser)
                .isEqualTo(user1)
                .hasFieldOrPropertyWithValue("name", "UpdatedTestUser1")
                .hasFieldOrPropertyWithValue("login", "updatedTestUser1")
                .hasFieldOrPropertyWithValue("email", "updatedTestUser1@example.com")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 2, 2));

    }

    @Test
    public void addToFriendsTest() {
        userController.create(user1);
        userController.create(user2);
        long userId = user1.getId();
        long friendId = user2.getId();

        userController.addToFriends(userId, friendId);

        SqlRowSet result = jdbcTemplate.queryForRowSet("SELECT * FROM friendships WHERE user_id = ? AND friend_id = ?", userId, friendId);
        assertTrue(result.next());
    }

    @Test
    public void deleteInviteFromFriendTest() {
        userController.create(user1);
        userController.create(user2);
        long userId = user1.getId();
        long friendId = user2.getId();

        userController.addToFriends(userId, friendId);

        userController.deleteInviteToFriend(userId, friendId);

        SqlRowSet result = jdbcTemplate.queryForRowSet("SELECT * FROM friendships WHERE user_id = ? AND friend_id = ?", userId, friendId);
        assertFalse(result.next());
    }

    @Test
    public void getAllFriendsTest() {
        userController.create(user1);
        userController.create(user2);
        long userId = user1.getId();
        long friendId = user2.getId();

        userController.addToFriends(userId, friendId);

        List<User> friends = userController.getAllFriends(userId);
        assertEquals(1, friends.size());
        assertEquals(user2, friends.get(0));
    }

    @Test
    public void friendsOfBothUsersTest() {
        userController.create(user1);
        userController.create(user2);
        userController.create(user3);
        userController.addToFriends(user1.getId(), user3.getId());
        userController.addToFriends(user2.getId(), user3.getId());

        List<User> friendsOfBoth = userController.getFriendsOfBothUsers(user1.getId(), user2.getId());
        assertEquals(friendsOfBoth.size(), 1);
        assertEquals(friendsOfBoth.get(0), user3);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testUpdateWithWrongId() {
        userController.create(user1);
        user1.setId(666);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userController.update(user1));
        assertEquals("Пользователя с выбранным id не существует.", exception.getMessage());
    }

    @Test
    public void testAddToFriendWhenAlreadyFriends() {
        userController.create(user1);
        userController.create(user2);
        userController.addToFriends(user1.getId(), user2.getId());
        UsersAlreadyFriendsException exception = assertThrows(UsersAlreadyFriendsException.class,
                () -> userController.addToFriends(user1.getId(), user2.getId()));
        assertEquals("Пользователь уже в списке друзей.", exception.getMessage());
    }

    @Test
    public void testDeleteFriendWhenIsNotFriend() {
        userController.create(user1);
        userController.create(user2);
        UsersNotFriendsException exception = assertThrows(UsersNotFriendsException.class,
                () -> userController.deleteInviteToFriend(user1.getId(), user2.getId()));
        assertEquals("Выбранный пользователь не в списке друзей.", exception.getMessage());
    }

    @Test
    public void createUserWithEmptyEmail() {
        user1.setEmail("");
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user1));
        assertEquals("e-mail не должен быть пустым.", exception.getMessage());
    }

    @Test
    public void createUserWithWrongEmail() {
        user1.setEmail("emailwithoutsobaka");
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user1));
        assertEquals("Некорректный формат e-mail. Необходим символ @.", exception.getMessage());
    }

    @Test
    public void createUserWithEmptyLogin() {
        user1.setLogin("");
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user1));
        assertEquals("Логин не должен быть пустым.", exception.getMessage());
    }

    @Test
    public void createUserWithBlank() {
        user1.setLogin("login with blank");
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user1));
        assertEquals("Логин не должен содержать пробелы.", exception.getMessage());
    }

    @Test
    public void createUserWithEmptyDate() {
        user1.setBirthday(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user1));
        assertEquals("Дата рождения не может быть в будущем или пустой.", exception.getMessage());
    }

    @Test
    public void createUserWithFutureDate() {
        user1.setBirthday(LocalDate.of(3333, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user1));
        assertEquals("Дата рождения не может быть в будущем или пустой.", exception.getMessage());
    }
}
