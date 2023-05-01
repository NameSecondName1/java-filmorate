package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Genre.GenreRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage{
    private final JdbcTemplate jdbcTemplate;
    final FilmRowMapper filmRowMapper;
    final GenreRowMapper genreRowMapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper, GenreRowMapper genreRowMapper){
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    public Map<Long, Film> getAllFilms() {
        Map<Long, Film> films = new HashMap<>();
        String sql = "select * from films AS F left outer join ratings AS R ON F.rating_id = R.rating_id";
        List<Film> filmList = jdbcTemplate.query(sql, filmRowMapper);
        for (Film film : filmList) {
            films.put(film.getId(), film);
        }
        String genresSql = "select * from film_genres AS FG left outer join genres AS G ON FG.genre_id = G.genre_id";
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet(genresSql);
        while (genresRows.next()) {
            films.get(genresRows.getLong("film_id")).getGenres().add(new Genre(
                    genresRows.getLong("genre_id"),
                    genresRows.getString("genre_name")
            ));
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
            insertGenres(film.getGenres(), generatedId);
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
            Map<Long, Genre> uniqGenres = new HashMap<>();
            for (Genre element : film.getGenres()) {
                if (!uniqGenres.containsKey(element.getId())) {
                    uniqGenres.put(element.getId(), element);
                }
            }
            Set<Genre> uniqSet = new LinkedHashSet<>(uniqGenres.values());
            film.setGenres(uniqSet);
            updateGenres(film.getGenres(), film.getId());
        }
        return film;
    }

    @Override
    public Film getFilmById(long id) {
        String sql = "SELECT * FROM films AS F " +
                "LEFT OUTER JOIN ratings AS R ON F.rating_id = R.rating_id " +
                "WHERE id = ?";
        Film film = jdbcTemplate.queryForObject(sql, new Object[] {id}, filmRowMapper);

        sql = "SELECT * FROM film_genres AS FG " +
                "LEFT OUTER JOIN genres AS G ON FG.genre_id = G.genre_id " +
                "WHERE film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, new Object[] {id}, genreRowMapper);
        film.setGenres(new LinkedHashSet<>(genres));
        return film;
    }

   private void updateGenres(Set<Genre> genresId, long id) {
       String sqlDelete = "DELETE FROM film_genres WHERE film_id = ?";
       jdbcTemplate.update(sqlDelete, id);
       insertGenres(genresId, id);
   }

    private void insertGenres(Set<Genre> genresId, long id) {
        if (!genresId.isEmpty()) {
            List<Object[]> batchArgs = new ArrayList<>();
            for (Genre genre : genresId) {
                batchArgs.add(new Object[] { id, genre.getId() });
            }
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }

    @Override
    public boolean isContainId(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where id = ?", id);
        return filmRows.next();
    }

}


