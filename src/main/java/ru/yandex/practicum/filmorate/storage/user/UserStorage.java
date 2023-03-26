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


    void addToFriends(long fromId, long toId);
    boolean isAlreadySendInvite(long fromId, long toId);
    void deleteInviteToFriend(long fromId, long toId);
    Set<Long> getAllFriends(long id);
    Set<Long> friendsOfBothUsers (long firstId, long secondId);
    List<User> getUsersByIds(Set<Long> friends);
}
