package ru.yandex.practicum.filmorate.storage.Genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFountException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Component
public class GenresDbStorage implements GenresStorage{
    private final JdbcTemplate jdbcTemplate;

    public GenresDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

@Override
    public void updateGenres(Set<Genre> genresId, long id) {
   /*     Set<Genre> genresFromDb = new LinkedHashSet<>();
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
        insertGenres(genresId, id);*/
   // List<Map<String, Object>> filmGenres = jdbcTemplate.queryForList("SELECT * FROM film_genres WHERE film_id = ?", id);

    SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film_genres where film_id = ?", id);

    Set<Genre> genresToAdd = genresId;
    Set<Genre> genresToRemove = new HashSet<>();

    while (filmRows.next()) {
        Genre genre = new Genre(filmRows.getInt("genre_id"));
        if (genresToAdd.contains(genre)) {
            genresToAdd.remove(genre);
        } else {
            genresToRemove.add(genre);
        }
    }

     if (!genresToRemove.isEmpty()) {
        List<Object[]> batchArgs = new ArrayList<>();
        for (Genre genre : genresToRemove) {
            batchArgs.add(new Object[]{id, genre.getId()});
          }
        String sql = "DELETE FROM film_genres WHERE film_id = ? AND genre_id = ?";
        jdbcTemplate.batchUpdate(sql, batchArgs);
        }

    insertGenres(genresToAdd, id);
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
    public void insertGenres(Set<Genre> genresId, long id) {
  /*      for (Genre element : genresId) {
            String sqlQuery = "insert into film_genres(film_id, genre_id)" +
                    "values (?, ?)";
            jdbcTemplate.update(sqlQuery,
                    id,
                    element.getId()
            );
        }*/

        if (!genresId.isEmpty()) {
            List<Object[]> batchArgs = new ArrayList<>();
            for (Genre genre : genresId) {
                batchArgs.add(new Object[] { id, genre.getId() });
            }
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }
    
}
