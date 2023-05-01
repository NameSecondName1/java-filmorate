package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.RatingController;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.Genre.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.Genre.GenresDbStorage;
import ru.yandex.practicum.filmorate.storage.Likes.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.Rating.RatingRowMapper;
import ru.yandex.practicum.filmorate.storage.Rating.RatingsDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserRowMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RatingControllerTests {
    RatingController ratingController;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    FilmRowMapper filmRowMapper;
    @Autowired
    GenreRowMapper genreRowMapper;
    @Autowired
    UserRowMapper userRowMapper;
    @Autowired
    RatingRowMapper ratingRowMapper;

    @BeforeEach
    public void setUp() {
        ratingController = new RatingController(
                new FilmService(
                        new FilmDbStorage(jdbcTemplate, filmRowMapper, genreRowMapper),
                        new UserDbStorage(jdbcTemplate, userRowMapper),
                        new LikesDbStorage(jdbcTemplate, filmRowMapper),
                        new GenresDbStorage(jdbcTemplate, genreRowMapper),
                        new RatingsDbStorage(jdbcTemplate, ratingRowMapper)
                )
        );
        jdbcTemplate.execute("DELETE FROM ratings");
        jdbcTemplate.execute("INSERT INTO ratings (rating_id, rating_name) VALUES (1, 'rating1')");
        jdbcTemplate.execute("INSERT INTO ratings (rating_id, rating_name) VALUES (2, 'rating2')");
    }

    @Test
    public void testGetRatings() {
        List<Rating> ratings = ratingController.getRatings();
        assertEquals(2, ratings.size());
        assertEquals("rating1", ratings.get(0).getName());
        assertEquals("rating2", ratings.get(1).getName());
    }

    @Test
    public void testGetRatingById() {
        Rating rating = ratingController.getRatingById(1);
        assertNotNull(rating);
        assertEquals(1, rating.getId());
        assertEquals("rating1", rating.getName());
    }

    @Test
    public void testGetRatingByIdThrowsExceptionWhenNotFound() {
        assertThrows(EntityNotFoundException.class, () -> ratingController.getRatingById(999));
    }
}
