package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserStorage {
    Map<Long, User> getAllUsers();
    User create(User user);
    User update(User user);
    boolean isContainId(long id);
    User getUserById(long id);
}
