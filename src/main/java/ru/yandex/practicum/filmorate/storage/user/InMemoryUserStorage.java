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
    public boolean isAlreadySendInvite(long fromId, long toId) {
        return true;
    }
    @Override
    public void deleteInviteToFriend(long fromId, long toId){
    }
    @Override
    public Set<Long> getAllFriends(long id) {
        return null;
    }
    @Override
    public Set<Long> friendsOfBothUsers (long firstId, long secondId) {
        return null;
    }
    @Override
    public List<User> getUsersByIds(Set<Long> friends) {
        return null;
    }
}
