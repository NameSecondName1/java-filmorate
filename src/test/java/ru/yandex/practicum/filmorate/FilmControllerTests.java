package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.Genre.GenresDbStorage;
import ru.yandex.practicum.filmorate.storage.Likes.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.Rating.RatingsDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.filmorate.Constants.DESCENDING_ORDER;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTests {
    Film film1;
    Film film2;
    User user1;
    User user2;
    Genre testGenre1;
    Genre testGenre2;
    FilmController filmController;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @BeforeEach
    public void BeforeEach() {
        filmController = new FilmController(new FilmService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), new LikesDbStorage(jdbcTemplate),
                new GenresDbStorage(jdbcTemplate), new RatingsDbStorage(jdbcTemplate)));

        jdbcTemplate.execute("delete from LIKES");
        jdbcTemplate.execute("delete from  film_genres");
        jdbcTemplate.execute("delete from films");
        jdbcTemplate.execute("delete from USERS");

        film1 = new Film(1, "The Matrix", "la-la-la", LocalDate.of(1999, 01, 01),
                200, new Rating(1, "G"), new LinkedHashSet<>());
        film2 = new Film(2, "ko-ko-ko", "ta-ta-ta", LocalDate.of(1990, 02, 11),
                205, new Rating(2, "PG"), new LinkedHashSet<>());

        user1 = new User(1, "testuser1@example.com", "testUser1",
                "TestUser1", LocalDate.of(2000, 1, 1));
        user2 = new User(2, "testuser2@example.com", "testUser2",
                "TestUser2", LocalDate.of(2000, 2, 2));
        jdbcTemplate.update("INSERT INTO USERS (id, name, LOGIN, EMAIL, BIRTHDAY) " +
                "VALUES (1, 'TestUser1', 'testUser1', 'testuser1@example.com', '2000-1-1')");
        jdbcTemplate.update("INSERT INTO USERS (id, name, LOGIN, EMAIL, BIRTHDAY) " +
                "VALUES (2, 'TestUser2', 'testUser2', 'testuser2@example.com', '2000-2-2')");

        testGenre1 = new Genre(1, "Комедия");
        testGenre2 = new Genre(2, " Драма");
    }

    @Test
    public void testGetFilms() {
        // Сохраняем фильмы в базу данных
        filmController.create(film1);
        filmController.create(film2);
        List<Film> films = filmController.getAllFilms();

        // проверим, что список не пустой
        assertNotNull(films);
        assertEquals(2, films.size());
        assertTrue(films.contains(film1));

        // проверим пограничные случаи
        Film filmFromDb1 = films.get(0);
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
        Film savedFilm = filmController.create(film1);
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
        Film savedFilmWithGenres = filmController.create(savedFilm);
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
        filmController.create(film1);

        // Изменяем значения фильма и обновляем его в базе данных
        film1.setName("ChangedName");
        film1.setDescription("ChangedDesc");
        film1.setReleaseDate(LocalDate.of(1997, 01, 01));
        film1.setDuration(111);
        film1.setMpa(new Rating(2, "PG"));
        filmController.update(film1);
        // Получаем фильм из базы данных и проверяем, что значения обновлены успешно
        Film updatedFilm = filmController.getFilmById(film1.getId());
        assertEquals(film1.getName(), updatedFilm.getName());
        assertEquals(film1.getDescription(), updatedFilm.getDescription());
        assertEquals(film1.getReleaseDate(), updatedFilm.getReleaseDate());
        assertEquals(film1.getDuration(), updatedFilm.getDuration());
        assertEquals(film1.getMpa().getId(), updatedFilm.getMpa().getId());
        assertEquals(film1.getGenres(), updatedFilm.getGenres());
    }

    @Test
    public void testGetFilmById() {
        filmController.create(film1);

        Film retrievedFilm = filmController.getFilmById(film1.getId());

        assertNotNull(retrievedFilm);
        assertEquals(film1.getId(), retrievedFilm.getId());
        assertEquals(film1.getName(), retrievedFilm.getName());
        assertEquals(film1.getDescription(), retrievedFilm.getDescription());
        assertEquals(film1.getReleaseDate(), retrievedFilm.getReleaseDate());
        assertEquals(film1.getDuration(), retrievedFilm.getDuration());
        assertEquals(film1.getMpa().getId(), retrievedFilm.getMpa().getId());
        assertEquals(film1.getGenres(), retrievedFilm.getGenres());
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void getPopularFilmsTest() {
        filmController.create(film1);
        filmController.create(film2);
        filmController.addLike(film1.getId(), user1.getId());
        filmController.addLike(film1.getId(), user2.getId());
        filmController.addLike(film2.getId(), user1.getId());
        int count = 2;

        List<Film> popular = filmController.getPopularFilms(count, DESCENDING_ORDER);

        assertEquals(count, popular.size());
        assertEquals("The Matrix", popular.get(0).getName());
        assertEquals("ko-ko-ko", popular.get(1).getName());
    }

    @Test
    public void addLikeTest() {
        filmController.create(film1);
        filmController.create(film2);
        long filmId = film1.getId();
        long userId = user1.getId();

        filmController.addLike(filmId, userId);

        SqlRowSet result = jdbcTemplate.queryForRowSet("SELECT * FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
        assertTrue(result.next());
    }

    @Test
    public void deleteLikeTest() {
        filmController.create(film1);
        filmController.create(film2);
        long filmId = film1.getId();
        long userId = user1.getId();

        filmController.addLike(filmId, userId);
        filmController.deleteLike(filmId, userId);

        SqlRowSet result = jdbcTemplate.queryForRowSet("SELECT * FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
        assertFalse(result.next());
    }

}
