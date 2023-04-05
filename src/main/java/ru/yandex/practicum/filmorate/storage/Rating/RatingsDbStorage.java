package ru.yandex.practicum.filmorate.storage.Rating;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFountException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.ArrayList;
import java.util.List;

@Component
public class RatingsDbStorage implements RatingsStorage{
    private final JdbcTemplate jdbcTemplate;

    public RatingsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Rating> getRatings() {
        List<Rating> ratings = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from ratings");

        while (filmRows.next()) {
            Rating rating = new Rating(filmRows.getInt("rating_id"),
                    filmRows.getString("rating_name"));
            ratings.add(rating);
        }
        return ratings;
    }

    @Override
    public Rating getRatingById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from ratings where rating_id = ?", id);
        if (filmRows.next()) {
            return new Rating(id, filmRows.getString("rating_name"));
        }
        else {
            throw new EntityNotFountException("Не существует рейтинга с указанным id.");
        }
    }
}
