package ru.yandex.practicum.filmorate.storage.Rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@Component
public class RatingsDbStorage implements RatingsStorage{

    final RatingRowMapper ratingRowMapper;

    private final JdbcTemplate jdbcTemplate;

    public RatingsDbStorage(JdbcTemplate jdbcTemplate, RatingRowMapper ratingRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingRowMapper = ratingRowMapper;
    }

    @Override
    public List<Rating> getRatings() {
        String sql = "SELECT * FROM ratings";
        return jdbcTemplate.query(sql, ratingRowMapper);
    }

    @Override
    public Rating getRatingById(int id) {
        String sql = "SELECT * FROM ratings WHERE rating_id = ?";
        List<Rating> ratings = jdbcTemplate.query(sql, new Object[]{id}, ratingRowMapper);

        if (!ratings.isEmpty()) {
            return ratings.get(0);
        } else {
            throw new EntityNotFoundException("Не существует рейтинга с указанным id.");
        }
    }

}

