package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/films"})
@Slf4j
public class FilmController {

    InMemoryFilmStorage filmStorage;

    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
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
    public Film update(@RequestBody Film film) throws ValidationException {
        if (filmStorage.isContainId(film)) {
            if (isValid(film)) {
                log.info("Фильм с id = {} успешно обновлен.",film.getId());
            }
            return filmStorage.update(film);
        } else {
            log.debug("Фильма с id = {} не существует.",film.getId());
            throw new ValidationException("Фильма с выбранным id не существует.");
        }
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
