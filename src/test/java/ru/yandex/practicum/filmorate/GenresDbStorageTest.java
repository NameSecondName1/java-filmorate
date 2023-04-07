package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Genre.GenresDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenresDbStorageTest {
    @Autowired
    private GenresDbStorage genresDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("delete from genres");
        jdbcTemplate.execute("INSERT INTO genres (genre_id, genre_name) VALUES (1, 'Action')");
        jdbcTemplate.execute("INSERT INTO genres (genre_id, genre_name) VALUES (2, 'Comedy')");
        jdbcTemplate.execute("INSERT INTO genres (genre_id, genre_name) VALUES (3, 'Drama')");
    }

    @Test
    void getGenres_shouldReturnListOfGenres() {
        List<Genre> genres = genresDbStorage.getGenres();
        assertEquals(3, genres.size());
        assertEquals("Action", genres.get(0).getName());
        assertEquals("Comedy", genres.get(1).getName());
        assertEquals("Drama", genres.get(2).getName());
    }

    @Test
    void getGenreById_shouldReturnGenreById() {
        Genre genre = genresDbStorage.getGenreById(1L);
        assertEquals(1L, genre.getId());
        assertEquals("Action", genre.getName());
    }
}
