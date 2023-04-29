package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTests {
    @Autowired
    private UserDbStorage userStorage;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void beforeEach () {
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    public void testIsContainId() {
        User user = new User(1, "Test User", "testUser", "test@example.com", LocalDate.of(2000, 1, 1));
        boolean result = userStorage.isContainId(user.getId());
        assertThat(result).isFalse();
        userStorage.create(user);
        result = userStorage.isContainId(user.getId());
        assertThat(result).isTrue();
    }
}
