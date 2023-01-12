package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UsersAlreadyFriendsException;
import ru.yandex.practicum.filmorate.exception.UsersNotFriendsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    UserStorage userStorage;
    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers(){
        log.debug("Текущее количество пользователей: {}", userStorage.getAllUsers().size());
        return new ArrayList<>(userStorage.getAllUsers().values());
    }

    public User create(User user) {
        if (isValid(user)) {
            if ((user.getName() == null)||(user.getName().isBlank())) {
                user.setName(user.getLogin());
            }
        }
        log.info("Добавлен новый юзер: {}.", user.getName());
        return userStorage.create(user);
    }

    public User update(User user) {
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

    public User getUserById (long id) {
        if (!userStorage.isContainId(id)) {
            log.debug("Пользователя с id = {} не существует.", id);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        return userStorage.getUserById(id);
    }

    public void addToFriends(long firstId, long secondId) {
        if (!userStorage.isContainId(firstId)) {
            log.debug("Пользователя с id = {} не существует.", firstId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (!userStorage.isContainId(secondId)) {
            log.debug("Пользователя с id = {} не существует.", secondId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (isAlreadyFriends(firstId, secondId)) {
            log.debug("Пользователя с id = {} и id = {} уже друзья.", firstId, secondId);
            throw new UsersAlreadyFriendsException("Пользователи с выбранными id уже друзья.");
        } else {
            User firstUser = userStorage.getAllUsers().get(firstId);
            User secondUser = userStorage.getAllUsers().get(secondId);
            firstUser.getFriends().add(secondId);
            secondUser.getFriends().add(firstId);
            userStorage.update(firstUser);
            userStorage.update(secondUser);
            log.debug("Пользователь с id = {} и id = {} успешно добавлены в друзья.", firstId, secondId);
        }
    }

    public void removeFromFriends(long firstId, long secondId) {
        if (!userStorage.isContainId(firstId)) {
            log.debug("Пользователя с id = {} не существует.", firstId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (!userStorage.isContainId(secondId)) {
            log.debug("Пользователя с id = {} не существует.", secondId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (isAlreadyFriends(firstId, secondId)) {
            User firstUser = userStorage.getAllUsers().get(firstId);
            User secondUser = userStorage.getAllUsers().get(secondId);
            firstUser.getFriends().remove(secondId);
            secondUser.getFriends().remove(firstId);
            userStorage.update(firstUser);
            userStorage.update(secondUser);
            log.debug("Пользователи с id = {} и id = {} больше не друзья.", firstId, secondId);
        } else {
            log.debug("Пользователя с id = {} и id = {} не являются друзьями.", firstId, secondId);
            throw new UsersNotFriendsException("Пользователи с выбранными id не являются друзьями.");
        }
    }

    public List<User> getAllFriends(long id) {
        if (!userStorage.isContainId(id)) {
            log.debug("Пользователя с id = {} не существует.", id);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        log.debug("Запрошен список друзей юзера с id = {} .", id);
        return getUsersByIds(userStorage.getUserById(id).getFriends());
    }

    public List<User> friendsOfBothUsers (long firstId, long secondId) {
        if (!userStorage.isContainId(firstId)) {
            log.debug("Пользователя с id = {} не существует.", firstId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (!userStorage.isContainId(secondId)) {
            log.debug("Пользователя с id = {} не существует.", secondId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        Set<Long> idOfBothUsers = new HashSet<>();
        User firstUser = userStorage.getAllUsers().get(firstId);
        User secondUser = userStorage.getAllUsers().get(secondId);
        if (!firstUser.getFriends().isEmpty() && !secondUser.getFriends().isEmpty()) {
            for (Long element : firstUser.getFriends()) {
                if (secondUser.getFriends().contains(element)) {
                    idOfBothUsers.add(element);
                }
            }
        }
        log.debug("Запрошен список общих друзей юзеров с id = {} и id = {}.", firstId, secondId);
        return getUsersByIds(idOfBothUsers);
    }

    public boolean isAlreadyFriends (long firstId, long secondId) {
            return userStorage.getAllUsers().get(firstId).getFriends().contains(secondId);
    }

    public List<User> getUsersByIds(Set<Long> friends) {
        List<User> usersFromId = new ArrayList<>();
        for (Long elem: friends) {
            usersFromId.add(userStorage.getAllUsers().get(elem));
        }
        return usersFromId;
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
