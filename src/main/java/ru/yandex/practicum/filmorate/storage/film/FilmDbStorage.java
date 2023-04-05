package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFountException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.*;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;
    private static long globalId = 1;

    public FilmDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Map<Long, Film> getAllFilms() {
        Map<Long, Film> films = new HashMap<>();

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films AS F " +
                "left outer join ratings AS R ON F.rating_id = R.rating_id");
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select * from likes");
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
                    new ArrayList<>());
            films.put(film.getId(), film);
        }
        while (likesRows.next()) {
            films.get(likesRows.getLong("film_id")).getLikes().
                    add(likesRows.getLong("user_id"));
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
                film.getMpa().getId()
        );
        if (film.getGenres() != null) {
            insertGenres(film.getGenres(), film.getId());
        }
        return film;
    }

    private void insertGenres(List<Genre> genresId, long id) {
        for (Genre element : genresId) {
            String sqlQuery = "insert into film_genres(film_id, genre_id)" +
                    "values (?, ?)";
            jdbcTemplate.update(sqlQuery,
                    id,
                    element.getId()
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
        List<Genre> uniqSortedList = new ArrayList<>(uniqGenres.values());
        GenreComparator genreComparator = new GenreComparator();
        uniqSortedList.sort(genreComparator);

         film.setGenres(uniqSortedList);
         updateGenres(film.getGenres(), film.getId());

        }
        return film;
    }

    private void updateGenres(List<Genre> genresId, long id) {
        List<Genre> genresFromDb = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film_genres where film_id = ?", id);
        while (filmRows.next()) {
            int x = filmRows.getInt("genre_id");
            if (genresId.contains(new Genre(x))) {
                genresFromDb.add(new Genre(x));
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
                new ArrayList<>());

        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select * from likes where film_id = ?", id);
        while (likesRows.next()) {
            film.getLikes().add(likesRows.getLong("user_id"));
        }

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
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from genres ORDER BY genre_id ASC");
        while (filmRows.next()) {
            Genre genre = new Genre(filmRows.getInt("genre_id"),
                    filmRows.getString("genre_name"));
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
            throw new EntityNotFountException("Не существует жанра с указанным id.");
        }
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

/*    private RatingsMPA ratingMPAFromString (String ratingName) {
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
        return name;
    }*/
}

