package ru.yandex.practicum.filmorate.storage.Likes;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikesStorage {
    List<Film> getPopularFilms(int count);
    void addLike(long filmId, long userId);
    void deleteLike(long filmId, long userId);
}
