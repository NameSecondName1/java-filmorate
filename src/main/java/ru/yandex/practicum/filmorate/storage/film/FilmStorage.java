package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    Map<Long, Film> getAllFilms();
    Film create(Film film);
    Film update(Film film);
    boolean isContainId (long id);
    Film getFilmById (long id);
  //  void insertGenres(Set<Genre> genresId, long id);
  //  void updateGenres(Set<Genre> genresId, long id);
}
