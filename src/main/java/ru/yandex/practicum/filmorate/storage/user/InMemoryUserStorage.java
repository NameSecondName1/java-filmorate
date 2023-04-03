package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component ("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage{
    private long globalId = 1;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Map<Long, User> getAllUsers() {
      return users;
    }

    @Override
    public User create(User user) {
        user.setId(globalId);
        globalId++;
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean isContainId(long id) {
        return users.containsKey(id);
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    @Override
    public void addToFriends(long fromId, long toId) {
    }
    @Override
    public boolean isAlreadyFriend(long fromId, long toId) {
        return true;
    }
    @Override
    public void deleteFromFriend(long fromId, long toId){
    }
    @Override
    public List<User> getAllFriends(long id) {
        return null;
    }
    @Override
    public List<User> friendsOfBothUsers (long firstId, long secondId) {
        return null;
    }
    @Override
    public List<User> getUsersByIds(List<Long> friends) {
        return null;
    }
}
