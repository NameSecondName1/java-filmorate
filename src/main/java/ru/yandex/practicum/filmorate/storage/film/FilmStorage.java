package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;
import java.util.Optional;

public interface FilmStorage {
    Map<Long, Film> getAllFilms();
    Film create(Film film);
    Film update(Film film);
    boolean isContainId (long id);
    Film getFilmById (long id);











    Map<Integer, String> getGenres();
    Optional<String> getGenreById(int id);
    Map<Integer, String> getRatings();
    Optional<String> getRatingById(int id);
}
