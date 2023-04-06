package ru.yandex.practicum.filmorate.storage.Likes;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Component
public class LikesDbStorage implements LikesStorage{

    private final JdbcTemplate jdbcTemplate;

    public LikesDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> popular = new ArrayList<>();
        SqlRowSet likesSet = jdbcTemplate.queryForRowSet("select id, F.name, F.description, F.release_date, " +
                "F.duration, F.rating_id, R.rating_name, count(user_id) from films as F " +
                "left outer join likes as L on F.id = L.film_id " +
                "left outer join ratings as R on F.rating_id = R.rating_id " +
                "group by id " +
                "order by count(user_id) DESC " +
                "LIMIT ?", count);
        while (likesSet.next()) {
            Film film = new Film(
                    likesSet.getInt("id"),
                    likesSet.getString("name"),
                    likesSet.getString("description"),
                    likesSet.getDate("release_date").toLocalDate(),
                    likesSet.getInt("duration"),
                    new Rating(likesSet.getInt("rating_id"),
                            likesSet.getString("rating_name")),
                    new LinkedHashSet<>()
            );
            popular.add(film);
        }
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
