package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
/*    FilmController filmController;
    Film testFilm;

    @BeforeEach
    public void beforeEach() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        User testUser1 = User.builder().email("test1@mail").login("test1Login")
                .birthday(LocalDate.of(1991,12,12)).name("test1Name").build();
        User testUser2 = User.builder().email("test2@mail").login("test2Login")
                .birthday(LocalDate.of(1992,12,12)).name("test2Name").build();
        userStorage.create(testUser1);
        userStorage.create(testUser2);
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), userStorage));
        testFilm = new Film(1,"testName", "testDescr", LocalDate.of(2000,12,12),
                120, 4, Set.of(1,2));
       *//* testFilm = Film.builder()
                .name("testName")
                .description("testDescr")
                .releaseDate(LocalDate.of(2000,12,12))
                .duration(120).build();*//*

    }

    @Test
    public void testGetFilms() throws ValidationException {
        List<Film> testFilms = new ArrayList<>();
        assertEquals(filmController.getAllFilms(), new ArrayList<>());
        filmController.create(testFilm);
        testFilms.add(testFilm);
        assertEquals(filmController.getAllFilms(), testFilms);
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
    public void testUpdateFilmWithWrongId(){
        filmController.create(testFilm);
        Film wrongIdFilm = new Film(5000, "testName", "testDescr", LocalDate.of(2000,12,12),
                120, 4, Set.of(1,2));
        final FilmDoesNotExistException exception = assertThrows(FilmDoesNotExistException.class, () -> filmController.update(wrongIdFilm));
        assertEquals("Фильма с выбранным id не существует.", exception.getMessage());
    }

    @Test
    public void testGoodUpdateFilm() throws ValidationException {
        *//*filmController.create(testFilm);
        Film updateFilm = Film.builder().id(testFilm.getId()).name("testNameCHANGED").description("testDescrCHANGED")
                .releaseDate(LocalDate.of(1990,12,12)).duration(100).build();
        filmController.update(updateFilm);
        List<Film> testFilms = new ArrayList<>();
        testFilms.add(updateFilm);
        assertEquals(filmController.getAllFilms(), testFilms);*//*
    }

    @Test
    public void testGetFilmById() {
        final FilmDoesNotExistException exception = assertThrows(FilmDoesNotExistException.class,
                () -> filmController.getFilmById(5));
        assertEquals("Фильма с выбранным id не существует.", exception.getMessage());
        filmController.create(testFilm);
        assertEquals(testFilm, filmController.getFilmById(testFilm.getId()));
    }

    @Test
    public void testAddLike() throws AlreadyLikedException {
        filmController.create(testFilm);
        assertEquals(new HashSet<>(), filmController.getFilmById(testFilm.getId()).getLikes());
        filmController.addLike(testFilm.getId(), 1L);
        Set<Long> testLikes = new HashSet<>();
        testLikes.add(1L);
        assertEquals(testLikes, filmController.getFilmById(testFilm.getId()).getLikes());
        final AlreadyLikedException exception = assertThrows(AlreadyLikedException.class,
                () -> filmController.addLike(testFilm.getId(), 1L));
        assertEquals("Пользователь с выбранным id уже лайкал данный фильм.", exception.getMessage());
    }

    @Test
    public void testDeleteLike() throws AlreadyLikedException, NoLikeException {
        filmController.create(testFilm);
        final NoLikeException exception = assertThrows(NoLikeException.class,
                () -> filmController.deleteLike(testFilm.getId(), 1L));
        assertEquals("Пользователь с выбранным id не ставил лайк выбранному фильму.", exception.getMessage());
        filmController.addLike(testFilm.getId(), 1L);
        filmController.deleteLike(testFilm.getId(), 1L);
        assertEquals(new HashSet<>(), filmController.getFilmById(testFilm.getId()).getLikes());
    }

    @Test
    public void testGetPopularFilms() throws AlreadyLikedException {
      *//*  Film testFilm1 = Film.builder().name("test1Name").description("test1Descr")
                .releaseDate(LocalDate.of(2001,12,12)).duration(120).build();
        Film testFilm2 = Film.builder().name("test2Name").description("test2Descr")
                .releaseDate(LocalDate.of(2002,12,12)).duration(120).build();
        filmController.create(testFilm);
        filmController.create(testFilm1);
        filmController.create(testFilm2);
        filmController.addLike(testFilm1.getId(), 1);
        filmController.addLike(testFilm2.getId(), 1);
        filmController.addLike(testFilm2.getId(), 2);
        List<Film> testList = new ArrayList<>();
        testList.add(testFilm2);
        testList.add(testFilm1);
        testList.add(testFilm);
        assertEquals(testList, filmController.getPopularFilms(3, "desc"));

        testList.remove(testFilm1);
        testList.remove(testFilm2);
        testList.add(testFilm1);
        testList.add(testFilm2);
        assertEquals(testList, filmController.getPopularFilms(5, "asc"));

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> filmController.getPopularFilms(-1, "asc"));
        assertEquals("size", exception.getParameter());
        final IncorrectParameterException exception1 = assertThrows(IncorrectParameterException.class,
                () -> filmController.getPopularFilms(3, "WRONG"));
        assertEquals("sort", exception1.getParameter());*//*
    }*/
}
