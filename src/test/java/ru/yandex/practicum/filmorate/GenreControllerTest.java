package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.Genre.GenresDbStorage;
import ru.yandex.practicum.filmorate.storage.Likes.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.Rating.RatingsDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreControllerTest {
    GenreController genreController;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        genreController = new GenreController(new FilmService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), new LikesDbStorage(jdbcTemplate),
                new GenresDbStorage(jdbcTemplate), new RatingsDbStorage(jdbcTemplate)));
        jdbcTemplate.execute("delete from genres");
        jdbcTemplate.execute("INSERT INTO genres (genre_id, genre_name) VALUES (1, 'Action')");
        jdbcTemplate.execute("INSERT INTO genres (genre_id, genre_name) VALUES (2, 'Comedy')");
        jdbcTemplate.execute("INSERT INTO genres (genre_id, genre_name) VALUES (3, 'Drama')");
    }

    @Test
    void getGenres_shouldReturnListOfGenres() {
        List<Genre> genres = genreController.getGenres();
        assertEquals(3, genres.size());
        assertEquals("Action", genres.get(0).getName());
        assertEquals("Comedy", genres.get(1).getName());
        assertEquals("Drama", genres.get(2).getName());
    }

    @Test
    void getGenreById_shouldReturnGenreById() {
        Genre genre = genreController.getGenreById(1L);
        assertEquals(1L, genre.getId());
        assertEquals("Action", genre.getName());
    }
}
