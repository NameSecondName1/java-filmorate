package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.RatingDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.RatingsMPA;

import java.util.*;

@Slf4j
@Component ("filmDbStorage")
public class FilmDbStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;
    private static long globalId = 1;

    public FilmDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Map<Long, Film> getAllFilms() {
        Map<Long, Film> films = new HashMap<>();

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films");
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select * from likes");
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from film_genres");
        while (filmRows.next()) {
            Film film = new Film(
                    filmRows.getLong("id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    filmRows.getInt("rating_id"),
                    new HashSet<>());
            films.put(film.getId(), film);
        }
        while (likesRows.next()) {
            films.get(likesRows.getLong("film_id")).getLikes().
                    add(likesRows.getLong("user_id"));
        }
        while (genresRows.next()) {
            films.get(genresRows.getLong("film_id")).getGenresId().
                    add(genresRows.getInt("genre_id"));
        }
        return films;
    }

    @Override
    public Film create(Film film) {
        film.setId(globalId);
        globalId++;
        String sqlQuery = "insert into films(id, name, description, release_date, duration, rating_id) " +
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
        String sqlQuery = "update films set " +
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
                String sqlQuery = "delete from film_genres " +
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
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where id = ?", id);
        return filmRows.next();
    }

    @Override
    public Film getFilmById(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where id = ?", id);
        filmRows.next();
        Film film = new Film(
                filmRows.getLong("id"),
                filmRows.getString("name"),
                filmRows.getString("description"),
                filmRows.getDate("release_date").toLocalDate(),
                filmRows.getInt("duration"),
                filmRows.getInt("rating_id"),
                new HashSet<>());

        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select * from likes where film_id = ?", id);
        while (likesRows.next()) {
            film.getLikes().add(likesRows.getLong("user_id"));
        }

        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from film_genres where film_id = ?", id);
        while (genresRows.next()) {
            film.getGenresId().add(genresRows.getInt("genre_id"));
        }

        return film;
    }

    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from genres");
        while (filmRows.next()) {
            Genre genre = new Genre(filmRows.getInt("genre_id"), filmRows.getString("genre_name"));
            genres.add(genre);
        }
        return genres;
    }

    @Override
    public Genre getGenreById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from genres where genre_id = ?", id);
        if (filmRows.next()) {
            return new Genre(filmRows.getInt("genre_id"), filmRows.getString("genre_name"));
        } else {
            throw new GenreDoesNotExistException("Не существует жанра с указанным id.");
        }
    }

    @Override
    public List<Rating> getRatings() {
        List<Rating> ratings = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from ratings");

        while (filmRows.next()) {
            String ratingName = filmRows.getString("rating_name");
            switch (ratingName) {
                case "G":
                    Rating rating = new Rating(filmRows.getInt("rating_id"), RatingsMPA.G);
                    ratings.add(rating);
                    break;
                case "PG":
                    rating = new Rating(filmRows.getInt("rating_id"), RatingsMPA.PG);
                    ratings.add(rating);
                    break;
                case "PG_13":
                    rating = new Rating(filmRows.getInt("rating_id"), RatingsMPA.PG_13);
                    ratings.add(rating);
                    break;
                case "R":
                    rating = new Rating(filmRows.getInt("rating_id"), RatingsMPA.R);
                    ratings.add(rating);
                    break;
                case "NC_17":
                    rating = new Rating(filmRows.getInt("rating_id"), RatingsMPA.NC_17);
                    ratings.add(rating);
                    break;
            }
        }
        return ratings;
    }

    @Override
    public Rating getRatingById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from ratings where rating_id = ?", id);

        if (filmRows.next()) {
            String ratingName = filmRows.getString("rating_name");
            RatingsMPA name = null;

            switch (ratingName) {
                case "G":
                    name =  RatingsMPA.G;
                    break;
                case "PG":
                    name =  RatingsMPA.PG;
                    break;
                case "PG_13":
                    name =  RatingsMPA.PG_13;
                    break;
                case "R":
                    name =  RatingsMPA.R;
                    break;
                case "NC_17":
                    name = RatingsMPA.NC_17;
                    break;
            }
            return new Rating(id, name);
        }
        else {
            throw new RatingDoesNotExistException("Не существует рейтинга с указанным id.");
        }
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
