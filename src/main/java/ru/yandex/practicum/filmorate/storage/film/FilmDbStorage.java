package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component ("filmDbStorage")
public class FilmDbStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;
    private static long globalId = 2;

    public FilmDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Map<Long, Film> getAllFilms() {
        Map<Long, Film> films = new HashMap<>();
        Set<Long> likes = new HashSet<>();
        Set<Integer> genresId = new HashSet<>();

        Film film = new Film(0,null,null,null,0,0,null);
        long firstId = 1;

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film AS f" +
                "left outer join film_genres AS fg ON f.id = fg.film_id" +
                "left outer join likes AS l ON f.id = l.film_id" +
                "order by id asc");

        while (filmRows.next()) {
            if (filmRows.getLong("id") != firstId) {
                firstId = filmRows.getLong("id");
                film.setGenresId(genresId);
                film.setLikes(likes);
                films.put(film.getId(), film);
                likes.clear();
                genresId.clear();
            }
            if (filmRows.getObject("user_id", Long.class) != null) {
                likes.add(filmRows.getObject("user_id", Long.class));
            }
            if (filmRows.getObject("genre_id", Integer.class) != null) {
                genresId.add(filmRows.getObject("genre_id", Integer.class));
            }
            film.setId(filmRows.getLong("id"));
            film.setName(filmRows.getString("name"));
            film.setDescription(filmRows.getString("description"));
            film.setReleaseDate(filmRows.getDate("release_date").toLocalDate());
            film.setDuration(filmRows.getInt("duration"));
            film.setRatingId(filmRows.getInt("rating_id"));
        }
        return films;
    }

    @Override
    public Film create(Film film) {
        film.setId(globalId);
        globalId++;
        String sqlQuery = "insert into film(id, name, description, release_date, duration, rating_id) " +
                "values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRatingId()
        );
        insertGenres(film.getGenresId(), film.getId());
        return film;
    }

    private void insertGenres(Set<Integer> genresId, long id) {
        for (Integer element : genresId) {
            String sqlQuery = "insert into film_genres(film_id, genre_id)" +
                    "values (?, ?)";
            jdbcTemplate.update(sqlQuery,
                    id,
                    element
            );
        }
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update film set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRatingId(),
                film.getId()
                );
        updateGenres(film.getGenresId(), film.getId());
        return film;
    }

    private void updateGenres(Set<Integer> genresId, long id) {
        Set<Integer> genresFromDb = new HashSet<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film_genres where film_id = ?", id);
        while (filmRows.next()) {
            int x = filmRows.getInt("genre_id");
            if (genresId.contains(x)) {
                genresFromDb.add(x);
            } else {
                String sqlQuery = "delete from film_genres" +
                        "where film_genres_id = ?";
                jdbcTemplate.update(sqlQuery,
                        filmRows.getInt("film_genres_id")
                );
            }
        }
        genresId.removeAll(genresFromDb);
        insertGenres(genresId, id);
    }

    @Override
    public boolean isContainId(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film where id = ?", id);
        return filmRows.next();
    }

    @Override
    public Film getFilmById(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film AS f" +
                        "left outer join film_genres AS fg ON f.id = fg.film_id" +
                        "left outer join likes AS l ON f.id = l.film_id" +
                        "where f.id = ?", id);
        Set<Long> likes = new HashSet<>();
        Set<Integer> genresId = new HashSet<>();
        while (filmRows.next()) {
            if (filmRows.getObject("user_id", Long.class) != null) {
                likes.add(filmRows.getObject("user_id", Long.class));
            }
            if (filmRows.getObject("genre_id", Integer.class) != null) {
                genresId.add(filmRows.getObject("genre_id", Integer.class));
            }
        }
        filmRows.first();
        Film film = new Film(
                filmRows.getLong("id"),
                filmRows.getString("name"),
                filmRows.getString("description"),
                filmRows.getDate("release_date").toLocalDate(),
                filmRows.getInt("duration"),
                filmRows.getInt("rating_id"),
                genresId
        );
        film.setLikes(likes);
        return film;
    }























    @Override
    public Map<Integer, String> getGenres() {
        Map<Integer, String> genres = new HashMap<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from genre");
        while (filmRows.next()) {
            genres.put(filmRows.getInt("genre_id"), filmRows.getString("genre_name"));
        }
        return genres;
    }

    @Override
    public Optional<String> getGenreById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from genre where genre_id = ?", id);
        return Optional.of(filmRows.getString("genre_name"));
    }

    @Override
    public Map<Integer, String> getRatings() {
        Map<Integer, String> ratings = new HashMap<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from rating");
        while (filmRows.next()) {
            ratings.put(filmRows.getInt("rating_id"), filmRows.getString("rating_name"));
        }
        return ratings;
    }

    @Override
    public Optional<String> getRatingById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from rating where rating_id = ?", id);
        return Optional.of(filmRows.getString("rating_name"));
    }
}
