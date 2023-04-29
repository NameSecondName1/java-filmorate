package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTests {
    Film film1;
    @Autowired
    private FilmDbStorage filmStorage;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void beforeEach() {
        jdbcTemplate.execute("delete from films");
        film1 = new Film(1, "The Matrix", "la-la-la", LocalDate.of(1999, 01, 01),
                200, new Rating(1, "G"), new LinkedHashSet<>());
    }

    @Test
    public void testIsContainIdReturnsTrueWhenFilmExists() {
        filmStorage.create(film1);
        boolean result = filmStorage.isContainId(film1.getId());
        assertTrue(result);
    }

    @Test
    public void testIsContainIdReturnsFalseWhenFilmDoesNotExist() {
        boolean result = filmStorage.isContainId(9999);
        assertFalse(result);
    }
}
