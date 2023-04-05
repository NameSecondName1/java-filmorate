package ru.yandex.practicum.filmorate.storage.Likes;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class LikesDbStorage implements LikesStorage{

    private final JdbcTemplate jdbcTemplate;
    FilmStorage filmStorage;

    public LikesDbStorage(JdbcTemplate jdbcTemplate, FilmStorage filmStorage){
        this.jdbcTemplate=jdbcTemplate;
        this.filmStorage=filmStorage;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> popular = new ArrayList<>();
        Map<Long, Film> allFilms = filmStorage.getAllFilms();
        SqlRowSet likesSet = jdbcTemplate.queryForRowSet("select id, count(user_id) from films as F " +
                "left outer join likes as L on F.id = L.film_id " +
                "group by id " +
                "order by count(user_id) DESC " +
                "LIMIT ?", count);
        while (likesSet.next()) {
            popular.add(allFilms.get(likesSet.getLong("id")));
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
