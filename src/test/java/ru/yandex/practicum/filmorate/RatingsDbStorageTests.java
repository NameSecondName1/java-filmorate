package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.Rating.RatingsDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RatingsDbStorageTests {
    @Autowired
    private RatingsDbStorage ratingsStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("DELETE FROM ratings");
        jdbcTemplate.execute("INSERT INTO ratings (rating_id, rating_name) VALUES (1, 'rating1')");
        jdbcTemplate.execute("INSERT INTO ratings (rating_id, rating_name) VALUES (2, 'rating2')");
    }

    @Test
    public void testGetRatings() {
        List<Rating> ratings = ratingsStorage.getRatings();
        assertEquals(2, ratings.size());
        assertEquals("rating1", ratings.get(0).getName());
        assertEquals("rating2", ratings.get(1).getName());
    }

    @Test
    public void testGetRatingById() {
        Rating rating = ratingsStorage.getRatingById(1);
        assertNotNull(rating);
        assertEquals(1, rating.getId());
        assertEquals("rating1", rating.getName());
    }

    @Test
    public void testGetRatingByIdThrowsExceptionWhenNotFound() {
        assertThrows(EntityNotFoundException.class, () -> ratingsStorage.getRatingById(999));
    }
}
