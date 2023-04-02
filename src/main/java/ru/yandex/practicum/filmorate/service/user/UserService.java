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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    UserStorage userStorage;
    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
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
     /*   if (userStorage.isContainId(user.getId())) {
            throw new UserAlreadyExistException("Пользователь с выбранным ID уже существует.");
        } else {
            log.info("Добавлен новый юзер: {}.", user.getName());
            return userStorage.create(user);
        }*/
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

    public void addToFriends(long fromId, long toId) {
        if (!userStorage.isContainId(fromId)) {
            log.debug("Пользователя с id = {} не существует.", fromId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (!userStorage.isContainId(toId)) {
            log.debug("Пользователя с id = {} не существует.", toId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (isAlreadySendInvite(fromId, toId)) {
            log.debug("Пользователь с id = {} уже отправлял запрос на дружбу юзеру с id = {}.", fromId, toId);
            throw new UsersAlreadyFriendsException("Пользователь уже отправлял запрос на добавление в друзья.");
        } else {
            log.debug("Пользователь с id = {} отправил запрос на дружбу юзеру с id = {}.", fromId, toId);
            userStorage.addToFriends(fromId, toId);
        }
    }

    public void deleteInviteToFriend(long firstId, long secondId) {
        if (!userStorage.isContainId(firstId)) {
            log.debug("Пользователя с id = {} не существует.", firstId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (!userStorage.isContainId(secondId)) {
            log.debug("Пользователя с id = {} не существует.", secondId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (isAlreadySendInvite(firstId, secondId)) {
            log.debug("Запрос на дружбу от юзера с id = {} юзеру с id = {} отменен.", firstId, secondId);
            userStorage.deleteInviteToFriend(firstId, secondId);
        } else {
            log.debug("Пользователь с id = {} не отправлял запрос на дружбу юзеру с id = {}.", firstId, secondId);
            throw new UsersNotFriendsException("Выбранный пользователь не отправлял запрос на дружбу.");
        }
    }

    public Set<Long> getAllFriends(long id) {
        if (!userStorage.isContainId(id)) {
            log.debug("Пользователя с id = {} не существует.", id);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        log.debug("Запрошен список id друзей юзера с id = {} .", id);
        return userStorage.getAllFriends(id);
    }

    public Set<Long> friendsOfBothUsers (long firstId, long secondId) {
        if (!userStorage.isContainId(firstId)) {
            log.debug("Пользователя с id = {} не существует.", firstId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
        if (!userStorage.isContainId(secondId)) {
            log.debug("Пользователя с id = {} не существует.", secondId);
            throw new UserDoesNotExistException("Пользователя с выбранным id не существует.");
        }
     /*   Set<Long> idOfBothUsers = new HashSet<>();
        User firstUser = userStorage.getAllUsers().get(firstId);
        User secondUser = userStorage.getAllUsers().get(secondId);
        if (!firstUser.getFriends().isEmpty() && !secondUser.getFriends().isEmpty()) {
            for (Long element : firstUser.getFriends()) {
                if (secondUser.getFriends().contains(element)) {
                    idOfBothUsers.add(element);
                }
            }
        }*/
        log.debug("Запрошен список id общих друзей юзеров с id = {} и id = {}.", firstId, secondId);
        return userStorage.friendsOfBothUsers(firstId, secondId);
    }

    public boolean isAlreadySendInvite(long fromId, long toId)
    {
            return userStorage.isAlreadySendInvite(fromId, toId);
    }

    public List<User> getUsersByIds(Set<Long> friends) {
        log.debug("Запрошен список юзеров по переданному множеству id.");
        return userStorage.getUsersByIds(friends);
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
