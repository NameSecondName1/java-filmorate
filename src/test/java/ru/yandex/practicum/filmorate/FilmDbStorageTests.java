package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTests {
    Film film1;
    Film film2;
    Genre testGenre1;
    Genre testGenre2;
    @Autowired
    private FilmDbStorage filmStorage;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void beforeEach() {
        jdbcTemplate.execute("delete from  film_genres");
        jdbcTemplate.execute("delete from films");

        film1 = new Film(1, "The Matrix", "la-la-la", LocalDate.of(1999, 01, 01),
                200, new Rating(1, "G"), new LinkedHashSet<>());
        film2 = new Film(2, "ko-ko-ko", "ta-ta-ta", LocalDate.of(1990, 02, 11),
                205, new Rating(2, "PG"), new LinkedHashSet<>());

        testGenre1 = new Genre(1, "Комедия");
        testGenre2 = new Genre(2, " Драма");
    }

    @Test
    public void testGetFilms() {
        // Сохраняем фильмы в базу данных
        filmStorage.create(film1);
        filmStorage.create(film2);
        Map<Long, Film> films = filmStorage.getAllFilms();

        // проверим, что список не пустой
        assertNotNull(films);
        assertEquals(2, films.size());
        assertTrue(films.containsValue(film1));

        // проверим пограничные случаи
        Film filmFromDb1 = films.get(film1.getId());
        assertNotNull(filmFromDb1);
        assertEquals(film1.getName(), filmFromDb1.getName());
        assertEquals(film1.getDescription(), filmFromDb1.getDescription());
        assertEquals(film1.getReleaseDate(), filmFromDb1.getReleaseDate());
        assertEquals(film1.getDuration(), filmFromDb1.getDuration());
        assertEquals(film1.getMpa(), filmFromDb1.getMpa());
        assertEquals(film1.getGenres(), filmFromDb1.getGenres());
    }

    @Test
    public void testAddFilm() {
        Film savedFilm = filmStorage.create(film1);
        // проверяем, что фильм успешно сохранен в базе данных
        assertNotNull(savedFilm.getId());
        assertEquals(film1.getName(), savedFilm.getName());
        assertEquals(film1.getDescription(), savedFilm.getDescription());
        assertEquals(film1.getReleaseDate(), savedFilm.getReleaseDate());
        assertEquals(film1.getDuration(), savedFilm.getDuration());
        assertEquals(film1.getMpa().getId(), savedFilm.getMpa().getId());
        // проверяем, что жанры фильма успешно сохранены в базе данных
        Set<Genre> testGenres = new LinkedHashSet<>();
        testGenres.add(new Genre(1, "Action"));
        testGenres.add(new Genre(2, "Comedy"));
        savedFilm.setGenres(testGenres);
        Film savedFilmWithGenres = filmStorage.create(savedFilm);
        assertNotNull(savedFilmWithGenres.getId());
        assertEquals(film1.getName(), savedFilmWithGenres.getName());
        assertEquals(film1.getDescription(), savedFilmWithGenres.getDescription());
        assertEquals(film1.getReleaseDate(), savedFilmWithGenres.getReleaseDate());
        assertEquals(film1.getDuration(), savedFilmWithGenres.getDuration());
        assertEquals(film1.getMpa().getId(), savedFilmWithGenres.getMpa().getId());
        assertEquals(testGenres, savedFilmWithGenres.getGenres());
    }

    @Test
    public void testUpdateFilm() {
        // Создаем фильм и сохраняем его в базе данных
        Set<Genre> genres = new LinkedHashSet<>();
        genres.add(testGenre1);
        genres.add(testGenre2);
        film1.setGenres(genres);
        filmStorage.create(film1);

        // Изменяем значения фильма и обновляем его в базе данных
        film1.setName("ChangedName");
        film1.setDescription("ChangedDesc");
        film1.setReleaseDate(LocalDate.of(1997, 01, 01));
        film1.setDuration(111);
        film1.setMpa(new Rating(2, "PG"));
        filmStorage.update(film1);
        // Получаем фильм из базы данных и проверяем, что значения обновлены успешно
        Film updatedFilm = filmStorage.getFilmById(film1.getId());
        assertEquals(film1.getName(), updatedFilm.getName());
        assertEquals(film1.getDescription(), updatedFilm.getDescription());
        assertEquals(film1.getReleaseDate(), updatedFilm.getReleaseDate());
        assertEquals(film1.getDuration(), updatedFilm.getDuration());
        assertEquals(film1.getMpa().getId(), updatedFilm.getMpa().getId());
        assertEquals(film1.getGenres(), updatedFilm.getGenres());
    }

    @Test
    public void testGetFilmById() {
        filmStorage.create(film1);

        Film retrievedFilm = filmStorage.getFilmById(film1.getId());

        assertNotNull(retrievedFilm);
        assertEquals(film1.getId(), retrievedFilm.getId());
        assertEquals(film1.getName(), retrievedFilm.getName());
        assertEquals(film1.getDescription(), retrievedFilm.getDescription());
        assertEquals(film1.getReleaseDate(), retrievedFilm.getReleaseDate());
        assertEquals(film1.getDuration(), retrievedFilm.getDuration());
        assertEquals(film1.getMpa().getId(), retrievedFilm.getMpa().getId());
        assertEquals(film1.getGenres(), retrievedFilm.getGenres());
    }

    @Test
    public void testIsContainIdReturnsTrueWhenFilmExists() {
        filmStorage.create(film1);
        boolean result = filmStorage.isContainId(film1.getId());
        assertTrue(result);
    }

    @Test
    public void testIsContainIdReturnsFalseWhenFilmDoesNotExist() {
        boolean result = filmStorage.isContainId(9999);
        assertFalse(result);
    }
}
