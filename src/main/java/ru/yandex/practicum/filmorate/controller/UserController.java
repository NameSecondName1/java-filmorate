package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/users"})
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private long id = 1;
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public User[] findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values().toArray(new User[0]);
    }

    @PostMapping
    public User create(@RequestBody User user) throws ValidationException {
        if (isValid(user)) {
            if ((user.getName() == null)||(user.getName().isBlank())) {
                user.setName(user.getLogin());
            }
            user.setId(id);
            log.info("Добавлен новый юзер: {}, присвоенный ему id = {}.", user.getName(),user.getId());
            id++;
            users.put(user.getId(), user);
        }
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) throws ValidationException {
        if (users.containsKey(user.getId())) {
            log.info("Юзер с id = {} успешно обновлен.",user.getId());
            users.put(user.getId(), user);
        } else {
            log.debug("Пользователя с id = {} не существует.",user.getId());
            throw new ValidationException("Пользователя с выбранным id не существует.");
        }
        return user;
    }

    public boolean isValid (User user) {
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
