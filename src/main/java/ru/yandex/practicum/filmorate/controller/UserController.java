package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UsersAlreadyFriendsException;
import ru.yandex.practicum.filmorate.exception.UsersNotFriendsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping({"/users"})
@Slf4j

public class UserController {
    UserStorage userStorage;
    UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.debug("Текущее количество пользователей: {}", userStorage.getAllUsers().size());

        return new ArrayList<>(userStorage.getAllUsers().values());
    }

    @PostMapping
    public User create(@RequestBody User user) throws ValidationException {
        if (isValid(user)) {
            if ((user.getName() == null)||(user.getName().isBlank())) {
                user.setName(user.getLogin());
            }
        }
        log.info("Добавлен новый юзер: {}.", user.getName());
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) throws ValidationException {
        if (userStorage.isContainId(user.getId())) {
            if (isValid(user)) {
                if ((user.getName() == null)||(user.getName().isBlank())) {
                    user.setName(user.getLogin());
                }
            }
            log.info("Юзер с id = {} успешно обновлен.",user.getId());
            return userStorage.update(user);
        } else {
            log.debug("Пользователя с id = {} не существует.",user.getId());
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addToFriends (@PathVariable long id, @PathVariable long friendId) throws UsersAlreadyFriendsException {
        if (!userStorage.isContainId(id)) {
            log.debug("Пользователя с id = {} не существует.", id);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (!userStorage.isContainId(friendId)) {
            log.debug("Пользователя с id = {} не существует.", friendId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (userService.isAlreadyFriends(id, friendId)) {
            log.debug("Пользователя с id = {} и id = {} уже друзья.", id, friendId);
            throw new UsersAlreadyFriendsException("Пользователи с выбранными id уже друзья.");
        } else {
            log.debug("Пользователь с id = {} и id = {} успешно добавлены в друзья.", id, friendId);
            userService.addToFriends(id, friendId);
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFromFriends(@PathVariable long id, @PathVariable long friendId) throws UsersNotFriendsException {
        if (!userStorage.isContainId(id)) {
            log.debug("Пользователя с id = {} не существует.", id);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (!userStorage.isContainId(friendId)) {
            log.debug("Пользователя с id = {} не существует.", friendId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (userService.isAlreadyFriends(id, friendId)) {
            log.debug("Пользователи с id = {} и id = {} больше не друзья.", id, friendId);
            userService.removeFromFriends(id, friendId);
        } else {
            log.debug("Пользователя с id = {} и id = {} не являются друзьями.", id, friendId);
            throw new UsersNotFriendsException("Пользователи с выбранными id не являются друзьями.");
        }
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends (@PathVariable long id) {
        if (!userStorage.isContainId(id)) {
            log.debug("Пользователя с id = {} не существует.", id);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        log.debug("Запрошен список друзей юзера с id = {} .", id);
        return userService.getUsersByIds(userStorage.getAllUsers().get(id).getFriends());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getFriendsOfBothUsers (@PathVariable long id, @PathVariable long otherId) {
        if (!userStorage.isContainId(id)) {
            log.debug("Пользователя с id = {} не существует.", id);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (!userStorage.isContainId(otherId)) {
            log.debug("Пользователя с id = {} не существует.", otherId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        log.debug("Запрошен список общих друзей юзеров с id = {} и id = {}.", id, otherId);
        return userService.friendsOfBothUsers(id, otherId);
    }

    @GetMapping("/{id}")
    public User getUserById (@PathVariable long id) {
        if (!userStorage.isContainId(id)) {
            log.debug("Пользователя с id = {} не существует.", id);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        return userStorage.getUserById(id);
    }



    private boolean isValid (User user) {
        if ((user.getEmail() == null)||(user.getEmail().equals(""))) {
            log.debug("У пользователя {} указан пустой e-mail.",user.getName());
            throw new ValidationException("e-mail не должен быть пустым.");
        } else if (!(user.getEmail().contains("@"))){
            log.debug("У пользователя {} некорректно указан e-mail: {}",user.getName(),user.getEmail());
            throw new ValidationException("Некорректный формат e-mail. Необходим символ @.");
        } else if ((user.getLogin() == null) || (user.getLogin().equals(""))) {
            log.debug("Пользователь пытается зарегестрироваться с пустым логином.");
            throw new ValidationException("Логин не должен быть пустым.");
        } else if (user.getLogin().contains(" ")) {
            log.debug("У пользователя c id = {} некорректно указан логин: {}. Пробелы недопустимы.",user.getId(), user.getLogin());
            throw new ValidationException("Логин не должен содержать пробелы.");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Пользователь {}, похоже, из будущего! Указанная дата рождения: {}.",user.getName(),user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем.");
        } else {
            return true;
        }
    }
}
