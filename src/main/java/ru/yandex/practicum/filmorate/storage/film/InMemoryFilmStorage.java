package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component ("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage{
    private static long globalId = 1;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Map<Long, Film> getAllFilms() {
        return films;
    }

    @Override
    public Film create(Film film) {
        film.setId(globalId);
        globalId++;
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public boolean isContainId (long id) {
        return films.containsKey(id);
    }

    @Override
    public Film getFilmById (long id) {
        return films.get(id);
    }








    @Override
    public Map<Integer, String> getGenres() {
        return null;
    }

    @Override
    public Optional<String> getGenreById(int id) {
        return Optional.empty();
    }

    @Override
    public Map<Integer, String> getRatings() {
        return null;
    }

    @Override
    public Optional<String> getRatingById(int id) {
        return Optional.empty();
    }
}
