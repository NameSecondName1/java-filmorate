package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UsersAlreadyFriendsException;
import ru.yandex.practicum.filmorate.exception.UsersNotFriendsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
  /*  UserController userController;
    User testUser1;
    User testUser2;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
        testUser1 = new User(1, "test1@mail", "test1Login", "test1Name", LocalDate.of(1991,12,12));
        testUser2 = new User(2, "test2@mail", "test2Login", "test2Name", LocalDate.of(1992,12,12));
    }

    @Test
    public void testGetUsers() throws ValidationException {
        List<User> testUsers = new ArrayList<>();
        assertEquals(userController.getAllUsers(), testUsers);
        userController.create(testUser1);
        testUsers.add(testUser1);
        assertEquals(userController.getAllUsers(), testUsers);
    }

    @Test
    public void testCreateUserWithEmptyEmail() throws ValidationException {
        testUser1.setEmail("");
        System.out.println(testUser1);

   *//*     final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        controller.create(testUser);
                    }
                }
        );*//*

        final ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(testUser1));
        assertEquals("e-mail не должен быть пустым.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithIncorrectEmail() throws ValidationException {
        testUser1.setEmail("emailWithoutDOG");
        System.out.println(testUser1);
        final ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(testUser1));
        assertEquals("Некорректный формат e-mail. Необходим символ @.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithEmptyLogin() {
        testUser1.setLogin("");
        System.out.println(testUser1);
        final ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(testUser1));
        assertEquals("Логин не должен быть пустым.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithSpaceInLogin() {
        testUser1.setLogin("Test Login");
        System.out.println(testUser1);
        final ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(testUser1));
        assertEquals("Логин не должен содержать пробелы.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithBirthdayInFuture() {
        testUser1.setBirthday(LocalDate.of(2222,12,12));
        System.out.println(testUser1);
        final ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(testUser1));
        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }

    @Test
    public void testUpdateUserWithWrongId() throws UserDoesNotExistException {
        userController.create(testUser1);
        User wrongIdUser = new User(5000, "test@mail", "testLogin", "testName", LocalDate.of(2000,12,12));
        final UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class, () -> userController.update(wrongIdUser));
        assertEquals("Пользователя с выбранным id не существует.", exception.getMessage());
    }

    @Test
    public void testGoodUpdateUser() throws ValidationException {
        userController.create(testUser1);
        User updateUser = new User(testUser1.getId(), "test@mailCHANGED", "testLoginGHANGED",
                "", LocalDate.of(1990,12,12));
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
        userController.create(testUser1);
        assertEquals(testUser1, userController.getUserById(1));
    }

    @Test
    public void testAddToFriend() throws UsersAlreadyFriendsException {
        userController.create(testUser1);
        userController.create(testUser2);
        userController.addToFriends(1,2);
        Set<Long> testSet = new HashSet<>();
        testSet.add(testUser2.getId());
        assertEquals(userController.getUserById(1).getFriends(), testSet);
    }

    @Test
    public void testRemoveFromFriends() throws UsersAlreadyFriendsException, UsersNotFriendsException {
        userController.create(testUser1);
        userController.create(testUser2);
        userController.addToFriends(testUser1.getId(), testUser2.getId());
        userController.deleteInviteToFriend(testUser1.getId(), testUser2.getId());
        assertEquals(new HashSet<>(), userController.getUserById(1).getFriends());

        final UsersNotFriendsException exception = assertThrows(UsersNotFriendsException.class,
                () -> userController.deleteInviteToFriend(testUser1.getId(), testUser2.getId()));
        assertEquals("Пользователи с выбранными id не являются друзьями.", exception.getMessage());
    }

    @Test
    public void testGetAllFriends() throws UsersAlreadyFriendsException {
        userController.create(testUser1);
        userController.create(testUser2);
        assertEquals(new ArrayList<>(), userController.getAllFriends(testUser1.getId()));
        userController.addToFriends(testUser1.getId(), testUser2.getId());
        List<User> testList = new ArrayList<>();
        testList.add(testUser2);
        assertEquals(testList, userController.getAllFriends(testUser1.getId()));
    }

    @Test
    public void testGetFriendsOfBothUsers() throws UsersAlreadyFriendsException {
        User testUser3 = new User(3, "test3@mail", "test3Login", "test3Name", LocalDate.of(1993,12,12));
        userController.create(testUser1);
        userController.create(testUser2);
        userController.create(testUser3);
        assertEquals(new ArrayList<>(), userController.getFriendsOfBothUsers(testUser1.getId(), testUser2.getId()));
        userController.addToFriends(testUser1.getId(), testUser2.getId());
        userController.addToFriends(testUser3.getId(), testUser2.getId());
        List<User> testList = new ArrayList<>();
        testList.add(testUser2);
        assertEquals(testList, userController.getFriendsOfBothUsers(testUser1.getId(), testUser3.getId()));
    }
*/
}
