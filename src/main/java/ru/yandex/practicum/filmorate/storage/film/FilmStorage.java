package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {
    Map<Long, Film> getAllFilms();
    Film create(Film film);
    Film update(Film film);
    boolean isContainId (long id);
    Film getFilmById (long id);
    List<Genre> getGenres();
    Genre getGenreById(int id);
    List<Rating> getRatings();
    Rating getRatingById(int id);

    void addLike(long filmId, long userId);
    void deleteLike(long filmId, long userId);
}
