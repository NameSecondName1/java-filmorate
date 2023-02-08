package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.Constants.DESCENDING_ORDER;
import static ru.yandex.practicum.filmorate.Constants.SORTS;

@Service
@Slf4j
public class FilmService {
    FilmStorage filmStorage;
    UserStorage userStorage;
    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() {
        log.debug("Текущее количество фильмов: {}", filmStorage.getAllFilms().size());
        return new ArrayList<>(filmStorage.getAllFilms().values());
    }

    public Film create(Film film){
        if (isValid(film)) {
            log.info("Добавлен новый фильм: {}, присвоенный ему id = {}.", film.getName(),film.getId()+1);
        }
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (filmStorage.isContainId(film.getId())) {
            if (isValid(film)) {
                log.info("Фильм с id = {} успешно обновлен.",film.getId());
            }
            return filmStorage.update(film);
        } else {
            log.debug("Фильма с id = {} не существует.",film.getId());
            throw new FilmDoesNotExistException("Фильма с выбранным id не существует.");
        }
    }

    public Film getFilmById (@PathVariable long id) {
        if (filmStorage.isContainId(id)) {
            return filmStorage.getFilmById(id);
        } else {
            log.debug("Фильма с id = {} не существует.",id);
            throw new FilmDoesNotExistException("Фильма с выбранным id не существует.");
        }
    }

    public Film addLike(long filmId, long userId) {
        if (!filmStorage.isContainId(filmId)) {
            log.debug("Фильма с id = {} не существует.",filmId);
            throw new FilmDoesNotExistException("Фильма с выбранным id не существует.");
        }
        if (!userStorage.isContainId(userId)) {
            log.debug("Пользователя с id = {} не существует.",userId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (filmStorage.getFilmById(filmId).getLikes().contains(userId)) {
            log.debug("Пользователя с id = {} уже ставил лайк выбранному фильму.",userId);
            throw new AlreadyLikedException("Пользователь с выбранным id уже лайкал данный фильм.");
        }
        filmStorage.getFilmById(filmId).getLikes().add(userId);
        return filmStorage.getFilmById(filmId);
    }

    public Film deleteLike(long filmId, long userId) {
        if (!filmStorage.isContainId(filmId)) {
            log.debug("Фильма с id = {} не существует.",filmId);
            throw new FilmDoesNotExistException("Фильма с выбранным id не существует.");
        }
        if (!userStorage.isContainId(userId)) {
            log.debug("Пользователя с id = {} не существует.",userId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (!filmStorage.getFilmById(filmId).getLikes().contains(userId)) {
            log.debug("Пользователь с id = {} не ставил лайк выбранному фильму.",userId);
            throw new NoLikeException("Пользователь с выбранным id не ставил лайк выбранному фильму.");
        }
        filmStorage.getFilmById(filmId).getLikes().remove(userId);
        return filmStorage.getFilmById(userId);
    }

    public List<Film> getPopularFilms(int count, String sort) {
        if (!SORTS.contains(sort)) {
            throw new IncorrectParameterException("sort");
        }
        if (count <= 0) {
            throw new IncorrectParameterException("size");
        }
        return new ArrayList<>(filmStorage.getAllFilms().values()).stream()
                .sorted((f0, f1) -> compare(f0, f1, sort))
                .limit(count)
                .collect(Collectors.toList());
    }


    private boolean isValid(Film film) {
        if ((film.getName() == null)||(film.getName().equals(""))) {
            log.debug("Фильм содержит пустое название.");
            throw new ValidationException("Название не может быть пустым.");
        } else if (film.getDescription().length() > 200) {
            log.debug("У фильма {} слишком длинное описание = {}, max = 200.",film.getName(),film.getDescription().length());
            throw new ValidationException("Длина поля description не должна превышать 200 символов.");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            log.debug("У фильма {} некорректная дата релиза: {}, min: 1895.12.28",film.getName(),film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть ранее, чем 28 декабря 1895 года. (1895.12.28)");
        } else if (film.getDuration() <= 0) {
            log.debug("У фильма {} некорректно указана длительность: {}, min = 1",film.getName(),film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        } else {
            return true;
        }
    }
    private int compare(Film f0, Film f1, String sort) {
        int result = f0.getLikes().size() - (f1.getLikes().size()); //прямой порядок сортировки
        if (sort.equals(DESCENDING_ORDER)) {
            result = -1 * result; //обратный порядок сортировки
        }
        return result;
    }














    public Map<Integer, String> getGenres() {
        log.debug("Текущее количество фильмов: {}", filmStorage.getGenres().size());
        return filmStorage.getGenres();
    }

    public Optional<String> getGenreById(int id) {
        return filmStorage.getGenreById(id);
    }

    public Map<Integer, String> getRatings() {
        return filmStorage.getRatings();
    }

    public Optional<String> getRatingById(int id) {
        return filmStorage.getRatingById(id);
    }
}
