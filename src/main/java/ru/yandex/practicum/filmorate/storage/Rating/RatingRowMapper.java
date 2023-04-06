package ru.yandex.practicum.filmorate.storage.Rating;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingRowMapper implements RowMapper<Rating> {
    @Override
    public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
          return new Rating(rs.getInt("rating_id"), rs.getString("rating_name"));
    }

}
