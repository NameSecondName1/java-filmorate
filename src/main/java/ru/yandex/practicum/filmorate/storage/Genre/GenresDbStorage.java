package ru.yandex.practicum.filmorate.storage.Genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Component
public class GenresDbStorage implements GenresStorage{

    final GenreRowMapper genreRowMapper;

    private final JdbcTemplate jdbcTemplate;

    public GenresDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper genreRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    public List<Genre> getGenres() {
        String sql = "SELECT * FROM genres ORDER BY genre_id ASC";
        List<Genre> genres = jdbcTemplate.query(sql, genreRowMapper);
        return genres;
    }

    @Override
    public Genre getGenreById(long id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, new Object[]{id}, genreRowMapper);
        if (!genres.isEmpty()) {
            return genres.get(0);
        } else {
            throw new EntityNotFoundException("Не существует жанра с указанным id.");
        }
    }

}
