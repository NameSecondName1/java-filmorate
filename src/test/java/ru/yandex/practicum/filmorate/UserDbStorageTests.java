package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTests {
    User user1;
    User user2;
    @Autowired
    private UserDbStorage userStorage;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @BeforeEach
    public void beforeEach () {
        jdbcTemplate.execute("DELETE FROM users");

        user1 = new User(1, "testuser1@example.com", "testUser1",
                "TestUser1", LocalDate.of(2000, 1, 1));

        user2 = new User(2, "testuser2@example.com", "testUser2",
                "TestUser2", LocalDate.of(2000, 2, 2));
    }


    @Test
    public void testGetUserById() {
        userStorage.create(user1);

        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(user1.getId()));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", user1.getId())
                );
    }

    @Test
    public void testCreateUser() {

        User createdUser = userStorage.create(user1);

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
    public void testGetAllUsers() {
        userStorage.create(user1);
        userStorage.create(user2);

        List<User> allUsers = userStorage.getAllUsers();

        assertThat(allUsers).containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    public void testUpdateUser() {
        userStorage.create(user1);

        user1.setName("UpdatedTestUser1");
        user1.setLogin("updatedTestUser1");
        user1.setEmail("updatedTestUser1@example.com");
        user1.setBirthday(LocalDate.of(2000, 2, 2));
        userStorage.update(user1);

        User updatedUser = userStorage.getUserById(user1.getId());

        assertThat(updatedUser)
                .isEqualTo(user1)
                .hasFieldOrPropertyWithValue("name", "UpdatedTestUser1")
                .hasFieldOrPropertyWithValue("login", "updatedTestUser1")
                .hasFieldOrPropertyWithValue("email", "updatedTestUser1@example.com")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 2, 2));
    }

    @Test
    public void testIsContainId() {
        User user = new User(1, "Test User", "testUser", "test@example.com", LocalDate.of(2000, 1, 1));
        userStorage.create(user);
        boolean result = userStorage.isContainId(user.getId());
        assertThat(result).isTrue();
    }
}
