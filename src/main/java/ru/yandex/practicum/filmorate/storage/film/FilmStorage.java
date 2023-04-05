package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    Map<Long, Film> getAllFilms();
    Film create(Film film);
    Film update(Film film);
    boolean isContainId (long id);
    Film getFilmById (long id);

    List<Rating> getRatings();
    Rating getRatingById(int id);

}
