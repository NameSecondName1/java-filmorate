package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFountException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.Genre.GenresStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage{
    private final JdbcTemplate jdbcTemplate;
    private final GenresStorage genresStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenresStorage genresStorage){
        this.jdbcTemplate=jdbcTemplate;
        this.genresStorage = genresStorage;
    }

    @Override
    public Map<Long, Film> getAllFilms() {
        Map<Long, Film> films = new HashMap<>();

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films AS F " +
                "left outer join ratings AS R ON F.rating_id = R.rating_id");
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from film_genres AS FG " +
                "left outer join genres AS G ON FG.genre_id = G.genre_id");

        while (filmRows.next()) {
            Film film = new Film(
                    filmRows.getLong("id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    new Rating(filmRows.getInt("rating_id"),
                            filmRows.getString("rating_name")),
                    new LinkedHashSet<>());
            films.put(film.getId(), film);
        }
        while (genresRows.next()) {
            films.get(genresRows.getLong("film_id")).getGenres().
                    add(new Genre(genresRows.getInt("genre_id"),
                            genresRows.getString("genre_name")));
        }
        return films;
    }

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO films (name, description, release_date, duration, rating_id) " +
                    "VALUES (?, ?, ?, ?, ?)", new String[] {"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        long generatedId = keyHolder.getKey().longValue();

        if (film.getGenres() != null) {
            genresStorage.insertGenres(film.getGenres(), generatedId);
        }
        film.setId(generatedId);
        return film;
    }


    @Override
    public Film update(Film film) {
        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
                );
        if (film.getGenres() != null){
            Map<Integer, Genre> uniqGenres = new HashMap<>();
            for (Genre element : film.getGenres()) {
                if (!uniqGenres.containsKey(element.getId())) {
                    uniqGenres.put(element.getId(), element);
                }
            }
            Set<Genre> uniqSet = new LinkedHashSet<>(uniqGenres.values());
            film.setGenres(uniqSet);
            genresStorage.updateGenres(film.getGenres(), film.getId());
        }
        return film;
    }


    @Override
    public boolean isContainId(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where id = ?", id);
        return filmRows.next();
    }

    @Override
    public Film getFilmById(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films AS F " +
                "left outer join ratings AS R ON F.rating_id = R.rating_id " +
                "where id = ?", id);
        filmRows.next();
        Film film = new Film(
                filmRows.getLong("id"),
                filmRows.getString("name"),
                filmRows.getString("description"),
                filmRows.getDate("release_date").toLocalDate(),
                filmRows.getInt("duration"),
                new Rating(filmRows.getInt("rating_id"),
                        filmRows.getString("rating_name")),
                new LinkedHashSet<>());

        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from film_genres AS FG " +
                "left outer join genres AS G ON FG.genre_id = G.genre_id " +
                "where film_id = ?", id);
        while (genresRows.next()) {
            film.getGenres().add(new Genre(genresRows.getInt("genre_id"),
                    genresRows.getString("genre_name")));
        }
        return film;
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

