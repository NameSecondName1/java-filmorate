package ru.yandex.practicum.filmorate.storage.Friends;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Component
public class FriendsDbStorage implements FriendsStorage{

    private final JdbcTemplate jdbcTemplate;
    UserStorage userStorage;

    public FriendsDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
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
        return userStorage.getUsersByIds(friendsId);
    }

    @Override
    public List<User> friendsOfBothUsers(long firstId, long secondId) {
        List<User> friendsOfBoth = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS AS U, FRIENDSHIPS AS F, FRIENDSHIPS AS O " +
                "where U.id = F.friend_id AND U.id = O.friend_id AND " +
                "F.USER_ID = ? AND O.USER_ID = ?", firstId, secondId);
        while (userRows.next()) {
            User user = new User(userRows.getLong("id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());
            friendsOfBoth.add(user);
        }
        return friendsOfBoth;
    }
}
