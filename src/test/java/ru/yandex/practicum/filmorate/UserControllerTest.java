package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class UserControllerTest {
    UserController userController;
    User testUser;
    @BeforeEach
    public void beforeEach(){
        userController = new UserController(null, null);// ПЕРЕСТАЛО РАБОТАТЬ, ПОТОМ ИСПРАВИТЬ
        testUser = User.builder().email("test@mail").login("testLogin")
                .birthday(LocalDate.of(2000,12,12)).name("testName").build();
    }

    @Test
    public void testGetUsers() throws ValidationException {
        List<User> testUsers = new ArrayList<>();
        assertEquals(userController.getAllUsers(), testUsers);
        testUsers.add(testUser);
        userController.create(testUser);
        assertEquals(userController.getAllUsers(), testUsers);
    }

    @Test
    public void testCreateUserWithEmptyEmail() throws ValidationException {
        testUser.setEmail("");
        System.out.println(testUser);
     /*
     final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        controller.create(testUser);
                    }
                }
        );
        */
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
    public void testUpdateUserWithWrongId() throws ValidationException {
        userController.create(testUser);
        User wrongIdUser = User.builder().id(5000).email("test@mail")
                .login("testLogin").birthday(LocalDate.of(2000,12,12)).name("testName").build();
        final ValidationException exception = assertThrows(ValidationException.class, () -> userController.update(wrongIdUser));
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
}
