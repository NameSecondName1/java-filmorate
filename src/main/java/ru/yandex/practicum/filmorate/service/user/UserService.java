package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addToFriends(long firstId, long secondId) {
        User firstUser = userStorage.getAllUsers().get(firstId);
        User secondUser = userStorage.getAllUsers().get(secondId);
            firstUser.getFriends().add(secondId);
            secondUser.getFriends().add(firstId);
            userStorage.update(firstUser);
            userStorage.update(secondUser);

    }

    public void removeFromFriends(long firstId, long secondId) {
        User firstUser = userStorage.getAllUsers().get(firstId);
        User secondUser = userStorage.getAllUsers().get(secondId);
            firstUser.getFriends().remove(secondId);
            secondUser.getFriends().remove(firstId);
            userStorage.update(firstUser);
            userStorage.update(secondUser);

    }

    public List<User> friendsOfBothUsers (long firstId, long secondId) {
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
}
