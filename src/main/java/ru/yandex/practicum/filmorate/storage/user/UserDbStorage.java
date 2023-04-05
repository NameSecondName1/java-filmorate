package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {

    private long globalId = 1;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, User> getAllUsers() {
        Map<Long, User> result = new HashMap<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS AS U ORDER BY U.ID ASC");
        SqlRowSet friendshipRows = jdbcTemplate.queryForRowSet("select * from FRIENDSHIPS");

        while (userRows.next()) {
            User user = new User(userRows.getLong("id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());
            result.put(user.getId(), user);
        }


        while (friendshipRows.next()) {
            if (friendshipRows.getString("friendship_status").equals("CONFIRMED")) {
                result.get(friendshipRows.getLong("user_id")).getFriendshipStatuses().
                        put(friendshipRows.getLong("friend_id"), Friendship.CONFIRMED);
            } else {
                result.get(friendshipRows.getLong("user_id")).getFriendshipStatuses().
                        put(friendshipRows.getLong("friend_id"), Friendship.UNCONFIRMED);
            }
        }


        return result;
    }

    @Override
    public User create(User user) {
        user.setId(globalId);
        globalId++;
        String sqlQuery = "insert into USERS(id, name, login, email, birthday) " +
                "values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getId(),
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday());
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "update USERS set " +
                "name = ?, login = ?, email = ?, birthday = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public boolean isContainId(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS where id = ?", id);
        return userRows.next();
    }

    @Override
    public User getUserById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);
        userRows.next();
        User user = new User(userRows.getLong("id"),
                userRows.getString("email"),
                userRows.getString("login"),
                userRows.getString("name"),
                userRows.getDate("birthday").toLocalDate());
        SqlRowSet friendshipRows = jdbcTemplate.queryForRowSet("select * from FRIENDSHIPS where user_id = ?", id);
        while (friendshipRows.next()) {
            if (friendshipRows.getString("friendship_status").equals("CONFIRMED")) {
                user.getFriendshipStatuses().put(friendshipRows.getLong("friend_id"), Friendship.CONFIRMED);
            } else {
                user.getFriendshipStatuses().put(friendshipRows.getLong("friend_id"), Friendship.UNCONFIRMED);
            }
        }
        return user;
    }

    @Override
    public void addToFriends(long userId, long friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendships where user_id = ? and friend_id = ?", friendId, userId);
        if (userRows.next()) {

            String sqlQuery = "update friendships set " +
                    "friendship_status = ?" +
                    "where friendship_id = ?";
            jdbcTemplate.update(sqlQuery,
                    "CONFIRMED",
                    userRows.getLong("friendship_id"));

            sqlQuery = "insert into friendships (user_id, friend_id, friendship_status)" +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    userId,
                    friendId,
                    "CONFIRMED");

        } else {
            String sqlQuery = "insert into friendships (user_id, friend_id, friendship_status)" +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    userId,
                    friendId,
                    "UNCONFIRMED");
        }
    }

    @Override
    public boolean isAlreadyFriend(long userId, long friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendships where user_id = ? and friend_id = ?", userId, friendId);
        return userRows.next();
    }

    @Override
    public void deleteFromFriend(long userId, long friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendships where user_id = ? and friend_id = ?", friendId, userId);
        if (userRows.next()) {
            String sqlQuery = "update friendships set " +
                    "friendship_status = ?" +
                    "where friendship_id = ?";
            jdbcTemplate.update(sqlQuery,
                    "UNCONFIRMED",
                    userRows.getLong("friendship_id"));
        }
        String sqlQuery = "delete from friendships " +
                "where user_id = ? and friend_id = ?";

        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getAllFriends(long id) {
        List<Long> friendsId = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendships where user_id = ?", id);
        while (userRows.next()) {
            long x = userRows.getLong("friend_id");
            friendsId.add(x);
        }
        return getUsersByIds(friendsId);
    }

    @Override
    public List<User> friendsOfBothUsers(long firstId, long secondId) {
        List<User> friendsOfBoth = new ArrayList<>();
  /*      List<User> firstFriends = getAllFriends(firstId);
        List<User> secondFriends = getAllFriends(secondId);
        for (User element : firstFriends) {
            if (secondFriends.contains(element)) {
                friendsOfBoth.add(element);
            }
        }*/
        Map<Long, User> allUsers = getAllUsers();
        Map<Long, Friendship> firstUserFriends = allUsers.get(firstId).getFriendshipStatuses();
        Map<Long, Friendship> secondUserFriends = allUsers.get(secondId).getFriendshipStatuses();
        List<Long> idOfBothFriends = new ArrayList<>();
        for (Long element : firstUserFriends.keySet()) {
            if (secondUserFriends.containsKey(element)) {
                idOfBothFriends.add(element);
            }
        }
        for (Long element : idOfBothFriends) {
            friendsOfBoth.add(allUsers.get(element));
        }
        return friendsOfBoth;
    }

    @Override
    public List<User> getUsersByIds(List<Long> friends) {
        List<User> users = new ArrayList<>();
        Map<Long, User> allUsers = getAllUsers();
        for (Long element : friends) {
            users.add(allUsers.get(element));
        }
        return users;
    }
}
