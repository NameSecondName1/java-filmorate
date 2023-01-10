package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UsersAlreadyFriendsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    UserController userController;
    User testUser;

    @BeforeEach
    public void beforeEach() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        userController = new UserController(userStorage, new UserService(userStorage));
        testUser = User.builder().email("test@mail").login("testLogin")
                .birthday(LocalDate.of(2000,12,12)).name("testName").build();
    }

    @Test
    public void testGetUsers() throws ValidationException {
        List<User> testUsers = new ArrayList<>();
        assertEquals(userController.getAllUsers(), testUsers);
        userController.create(testUser);
        testUsers.add(testUser);
        assertEquals(userController.getAllUsers(), testUsers);
    }

    @Test
    public void testCreateUserWithEmptyEmail() throws ValidationException {
        testUser.setEmail("");
        System.out.println(testUser);

   /*     final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        controller.create(testUser);
                    }
                }
        );*/


        final ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(testUser));
        assertEquals("e-mail не должен быть пустым.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithIncorrectEmail() throws ValidationException {
        testUser.setEmail("emailWithoutDOG");
        System.out.println(testUser);
        final ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(testUser));
        assertEquals("Некорректный формат e-mail. Необходим символ @.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithEmptyLogin() {
        testUser.setLogin("");
        System.out.println(testUser);
        final ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(testUser));
        assertEquals("Логин не должен быть пустым.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithSpaceInLogin() {
        testUser.setLogin("Test Login");
        System.out.println(testUser);
        final ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(testUser));
        assertEquals("Логин не должен содержать пробелы.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithBirthdayInFuture() {
        testUser.setBirthday(LocalDate.of(2222,12,12));
        System.out.println(testUser);
        final ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(testUser));
        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }

    @Test
    public void testUpdateUserWithWrongId() throws UserDoesNotExistException {
        userController.create(testUser);
        User wrongIdUser = User.builder().id(5000).email("test@mail")
                .login("testLogin").birthday(LocalDate.of(2000,12,12)).name("testName").build();
        final UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class, () -> userController.update(wrongIdUser));
        assertEquals("Пользователя с выбранным id не существует.", exception.getMessage());
    }

    @Test
    public void testGoodUpdateUser() throws ValidationException {
        userController.create(testUser);
        User updateUser = User.builder().id(testUser.getId()).email("test@mailCHANGED").login("testLoginGHANGED")
                .birthday(LocalDate.of(1990,12,12)).name("").build();
        userController.update(updateUser);
        List<User> testUsers = new ArrayList<>();
        testUsers.add(updateUser);
        assertEquals(userController.getAllUsers(), testUsers);
        assertEquals("testLoginGHANGED",updateUser.getName());
    }

    @Test
    public void testGetUserById() {
        final UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class, () -> userController.getUserById(5));
        assertEquals("Пользователя с выбранным id не существует.", exception.getMessage());
        userController.create(testUser);
        assertEquals(testUser, userController.getUserById(1));
    }

    @Test
    public void testAddToFriend() throws UsersAlreadyFriendsException {
        userController.create(testUser);
        User testUser1 = User.builder().email("test1@mail").login("test1Login")
                .birthday(LocalDate.of(2001,12,12)).name("test1Name").build();
        userController.create(testUser1);
        userController.addToFriends(1,2);
        Set<Long> testSet = new HashSet<>();
        testSet.add(testUser1.getId());
        System.out.println(userController.getUserById(1));
        assertEquals(userController.getUserById(1).getFriends(), testSet);
    }


    ///////// ДОДЕЛАТЬ ТЕСТЫ ТУТ И ПРОДОЛЖИТЬ В ФИЛЬМСАХ

}
