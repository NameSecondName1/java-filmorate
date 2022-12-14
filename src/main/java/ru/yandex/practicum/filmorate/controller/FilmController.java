package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/films"})
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private static int id = 1;
    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Map<Integer, Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films;
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ValidationException {
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
            film.setId(id);
            films.put(id, film);
            id++;
            log.info("Добавлен новый фильм: {}", film.getName());
            return film;
        }

    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Фильм с id = {} успешно изменен.",film.getId());
        } else {
            create(film);
        }
        return film;
    }
}
