package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.Constants.DESCENDING_ORDER;

@Service
public class FilmService {
    FilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addLike(long filmId, long userId) {
        filmStorage.getFilmById(filmId).getLikes().add(userId);
        return filmStorage.getFilmById(filmId);
    }

    public Film deleteLike(long filmId, long userId) {
        filmStorage.getFilmById(filmId).getLikes().remove(userId);
        return filmStorage.getFilmById(userId);
    }

    public List<Film> getPopularFilms(int count, String sort) {
        return new ArrayList<>(filmStorage.getAllFilms().values()).stream()
                .sorted((f0, f1) -> compare(f0, f1, sort))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film f0, Film f1, String sort) {
        int result = f0.getLikes().size() - (f1.getLikes().size()); //прямой порядок сортировки
        if (sort.equals(DESCENDING_ORDER)) {
            result = -1 * result; //обратный порядок сортировки
        }
        return result;
    }

}
