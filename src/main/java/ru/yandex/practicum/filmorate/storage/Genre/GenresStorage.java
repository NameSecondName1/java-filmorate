package ru.yandex.practicum.filmorate.storage.Genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenresStorage {
    List<Genre> getGenres();
    Genre getGenreById(int id);
    void insertGenres(Set<Genre> genresId, long id);
    void updateGenres(Set<Genre> genresId, long id);
}
