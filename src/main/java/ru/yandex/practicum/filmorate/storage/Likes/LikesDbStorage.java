package ru.yandex.practicum.filmorate.storage.Likes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmRowMapper;

import java.util.List;

@Component
public class LikesDbStorage implements LikesStorage{

    final FilmRowMapper filmRowMapper;

    private final JdbcTemplate jdbcTemplate;

    public LikesDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper){
        this.jdbcTemplate=jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "select id, F.name, F.description, F.release_date, " +
                "F.duration, F.rating_id, R.rating_name, count(user_id) from films as F " +
                "left outer join likes as L on F.id = L.film_id " +
                "left outer join ratings as R on F.rating_id = R.rating_id " +
                "group by id " +
                "order by count(user_id) DESC " +
                "LIMIT ?";

        List<Film> popular = jdbcTemplate.query(sql, new Object[]{count}, filmRowMapper);
        return popular;
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sqlQuery = "insert into likes (film_id, user_id)" +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                filmId,
                userId
        );
    }

    @Override
    public void deleteLike(long filmId, long userId){
        String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }
}
