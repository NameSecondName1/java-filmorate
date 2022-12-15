package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FilmControllerTest {
    FilmController filmController;
    Film testFilm;
    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        testFilm = Film.builder().name("testName").description("testDescr")
                .releaseDate(LocalDate.of(2000,12,12)).duration(120).build();
    }

    @Test
    public void testGetFilms() throws ValidationException {
        Set<Film> testFilms = new HashSet<>();
        assertEquals(filmController.findAll(), testFilms);
        testFilms.add(testFilm);
        filmController.create(testFilm);
        assertEquals(filmController.findAll(), testFilms);
    }

    @Test
    public void testCreateWithEmptyName() {
        testFilm.setName("");
        final ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(testFilm));
        assertEquals("Название не может быть пустым.", exception.getMessage());
    }

    @Test
    public void testCreateWithTooLongDescr() {
        testFilm.setDescription("qweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqwe" +
                "qweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqwe" +
                "qweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqwe" +
                "qweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqwe" +
                "qweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqweqwe");
        final ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(testFilm));
        assertEquals("Длина поля description не должна превышать 200 символов.", exception.getMessage());
    }

    @Test
    public void testCreateWithWrongReleaseDate() {
        testFilm.setReleaseDate(LocalDate.of(1800,12,12));
        final ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(testFilm));
        assertEquals("Дата релиза не может быть ранее, чем 28 декабря 1895 года. (1895.12.28)", exception.getMessage());
    }

    @Test
    public void testCreateWithWrongDuration() {
        testFilm.setDuration(0);
        final ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(testFilm));
        assertEquals("Продолжительность фильма должна быть положительной.", exception.getMessage());
    }

    @Test
    public void testUpdateFilmWithWrongId() throws ValidationException {
        filmController.create(testFilm);
        Film wrongIdFilm = Film.builder().id(5000).name("testName").description("testDescr")
                .releaseDate(LocalDate.of(2000,12,12)).duration(120).build();
        final ValidationException exception = assertThrows(ValidationException.class, () -> filmController.update(wrongIdFilm));
        assertEquals("Фильма с выбранным id не существует.", exception.getMessage());
    }

    @Test
    public void testGoodUpdateFilm() throws ValidationException {
        filmController.create(testFilm);
        Film updateFilm = Film.builder().id(testFilm.getId()).name("testNameCHANGED").description("testDescrCHANGED")
                .releaseDate(LocalDate.of(1990,12,12)).duration(100).build();
        filmController.update(updateFilm);
        Set<Film> testFilms = new HashSet<>();
        testFilms.add(updateFilm);
        assertEquals(filmController.findAll(), testFilms);
    }
}
