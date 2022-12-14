package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
public class FilmController {
  //  private static final Logger log = LoggerFactory.getLogger(FilmController.class);
  private Map<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public Map<Integer, Film> findAll() {
       // log.debug("Текущее количество фильмов: {}", posts.size());
        return films;
    }

    @PostMapping(value = "/film")
    public Film create(@RequestBody Film film) {
        films.put(film.getId(), film);
        //log.debug("Новый фильм: {}", film.getName());
        return film;
    }

    @PutMapping(value = "/film")
    public Film update(@RequestBody Film film) {
        films.put(film.getId(), film);
        //log.debug("Новый пост: {}", post.getAuthor());
        return film;
    }
}
