package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.filmorate.Constants.DESCENDING_ORDER;
import static ru.yandex.practicum.filmorate.Constants.SORTS;

@RestController
@RequestMapping({"/films"})
@Slf4j

public class FilmController {
    FilmStorage filmStorage;
    FilmService filmService;
    UserStorage userStorage;

    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage, FilmService filmService, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
        this.userStorage = userStorage;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.debug("Текущее количество фильмов: {}", filmStorage.getAllFilms().size());
        return new ArrayList<>(filmStorage.getAllFilms().values());
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ValidationException {
        if (isValid(film)) {
            log.info("Добавлен новый фильм: {}, присвоенный ему id = {}.", film.getName(),film.getId());
        }
        return filmStorage.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
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

    @GetMapping("/{id}")
    public Film getFilmById (@PathVariable long id) {
        if (filmStorage.isContainId(id)) {
            return filmStorage.getFilmById(id);
        } else {
            log.debug("Фильма с id = {} не существует.",id);
            throw new FilmDoesNotExistException("Фильма с выбранным id не существует.");
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable long id, @PathVariable long userId) {
        if (!filmStorage.isContainId(id)) {
            log.debug("Фильма с id = {} не существует.",id);
            throw new FilmDoesNotExistException("Фильма с выбранным id не существует.");
        }
        if (!userStorage.isContainId(userId)) {
            log.debug("Пользователя с id = {} не существует.",id);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable long id, @PathVariable long userId) {
        if (!filmStorage.isContainId(id)) {
            log.debug("Фильма с id = {} не существует.",id);
            throw new FilmDoesNotExistException("Фильма с выбранным id не существует.");
        }
        if (!userStorage.isContainId(userId)) {
            log.debug("Пользователя с id = {} не существует.",id);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms (
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count,
            @RequestParam(value = "sort", defaultValue = DESCENDING_ORDER, required = false) String sort
    ) {
        if (!SORTS.contains(sort)) {
            throw new IncorrectParameterException("sort");
        }
        if (count <= 0) {
            throw new IncorrectParameterException("size");
        }
        return filmService.getPopularFilms(count, sort);
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


}
