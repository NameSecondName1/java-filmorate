package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {
    UserStorage userStorage;
    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers(){
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
            throw new EntityNotFountException("Пользователя с выбранным id не существует.");
        }
    }

    public User getUserById (long id) {
        if (!userStorage.isContainId(id)) {
            log.debug("Пользователя с id = {} не существует.", id);
            throw new EntityNotFountException("Пользователя с выбранным id не существует.");
        }
        return userStorage.getUserById(id);
    }

    public void addToFriends(long userId, long friendId) {
        if (!userStorage.isContainId(userId)) {
            log.debug("Пользователя с id = {} не существует.", userId);
            throw new EntityNotFountException("Пользователя с выбранным id не существует.");
        }
        if (!userStorage.isContainId(friendId)) {
            log.debug("Пользователя с id = {} не существует.", friendId);
            throw new EntityNotFountException("Пользователя с выбранным id не существует.");
        }
        if (isAlreadyFriend(userId, friendId)) {
            log.debug("Пользователь с id = {} уже в списке друзей у юзера с id = {}.", friendId, userId);
            throw new UsersAlreadyFriendsException("Пользователь уже в списке друзей.");
        } else {
            log.debug("Пользователь с id = {} добавлен в список друзей к юзеру с id = {}.", friendId, userId);
            userStorage.addToFriends(userId, friendId);
        }
    }

    public void deleteInviteToFriend(long userId, long friendId) {
        if (!userStorage.isContainId(userId)) {
            log.debug("Пользователя с id = {} не существует.", userId);
            throw new EntityNotFountException("Пользователя с выбранным id не существует.");
        }
        if (!userStorage.isContainId(friendId)) {
            log.debug("Пользователя с id = {} не существует.", friendId);
            throw new EntityNotFountException("Пользователя с выбранным id не существует.");
        }
        if (isAlreadyFriend(userId, friendId)) {
            log.debug("У юзера с id = {} удален из списка друзей юзер с id = {}.", userId, friendId);
            userStorage.deleteFromFriend(userId, friendId);
        } else {
            log.debug("У пользователя с id = {} нет друга с id = {}.", userId, friendId);
            throw new UsersNotFriendsException("Выбранный пользователь не в списке друзей.");
        }
    }

    public List<User> getAllFriends(long id) {
        if (!userStorage.isContainId(id)) {
            log.debug("Пользователя с id = {} не существует.", id);
            throw new EntityNotFountException("Пользователя с выбранным id не существует.");
        }
        log.debug("Запрошен список id друзей юзера с id = {} .", id);
        return userStorage.getAllFriends(id);
    }

    public List<User> friendsOfBothUsers (long firstId, long secondId) {
        if (!userStorage.isContainId(firstId)) {
            log.debug("Пользователя с id = {} не существует.", firstId);
            throw new EntityNotFountException("Пользователя с выбранным id не существует.");
        }
        if (!userStorage.isContainId(secondId)) {
            log.debug("Пользователя с id = {} не существует.", secondId);
            throw new EntityNotFountException("Пользователя с выбранным id не существует.");
        }
        log.debug("Запрошен список id общих друзей юзеров с id = {} и id = {}.", firstId, secondId);
        return userStorage.friendsOfBothUsers(firstId, secondId);
    }

    public boolean isAlreadyFriend(long userId, long friendId)
    {
            return userStorage.isAlreadyFriend(userId, friendId);
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
