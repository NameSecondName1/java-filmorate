package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Likes.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikesDbStorageTests {
    User user1;
    User user2;
    Film film1;
    Film film2;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LikesDbStorage likesStorage;
    @Autowired
    private FilmDbStorage filmStorage;
    @Autowired
    private UserDbStorage userStorage;

    @BeforeEach
    public void beforeEach () {
        jdbcTemplate.execute("delete from likes");
        film1 = new Film(1, "The Matrix", "la-la-la", LocalDate.of(1999, 01, 01),
                200, new Rating(1, "G"), new LinkedHashSet<>());
        film2 = new Film(2, "ko-ko-ko", "ta-ta-ta", LocalDate.of(1990, 02, 11),
                205, new Rating(2, "PG"), new LinkedHashSet<>());
        user1 = new User(1, "testuser1@example.com", "testUser1",
                "TestUser1", LocalDate.of(2000, 1, 1));
        user2 = new User(2, "testuser2@example.com", "testUser2",
                "TestUser2", LocalDate.of(2000, 2, 2));
        filmStorage.create(film1);
        filmStorage.create(film2);
        userStorage.create(user1);
        userStorage.create(user2);
    }

    @Test
    public void getPopularFilmsTest() {
        jdbcTemplate.update("INSERT INTO likes (user_id, film_id) VALUES (1, 1)");
        jdbcTemplate.update("INSERT INTO likes (user_id, film_id) VALUES (2, 1)");
        jdbcTemplate.update("INSERT INTO likes (user_id, film_id) VALUES (1, 2)");

        int count = 2;

        List<Film> popular = likesStorage.getPopularFilms(count);

        assertEquals(count, popular.size());
        assertEquals("The Matrix", popular.get(0).getName());
        assertEquals("ko-ko-ko", popular.get(1).getName());
    }

    @Test
    public void addLikeTest() {
        long filmId = film1.getId();
        long userId = user1.getId();

        likesStorage.addLike(filmId, userId);

        SqlRowSet result = jdbcTemplate.queryForRowSet("SELECT * FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
        assertTrue(result.next());
    }

    @Test
    public void deleteLikeTest() {
        long filmId = film1.getId();
        long userId = user1.getId();

        likesStorage.addLike(filmId, userId);
        likesStorage.deleteLike(filmId, userId);

        SqlRowSet result = jdbcTemplate.queryForRowSet("SELECT * FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
        assertFalse(result.next());
    }
}
