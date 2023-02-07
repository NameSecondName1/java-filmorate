package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;


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

}
