package ru.yandex.practicum.filmorate.storage.Friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {
    void addToFriends(long fromId, long toId);
    boolean isAlreadyFriend(long fromId, long toId);
    void deleteFromFriend(long fromId, long toId);
    List<User> getAllFriends(long id);
    List<User> friendsOfBothUsers (long firstId, long secondId);
}
