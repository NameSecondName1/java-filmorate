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
@Component("userDbStorage")
public class UserDbStorage implements UserStorage{

    private long globalId = 1;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
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
                result.get(friendshipRows.getLong("to_id")).getFriendshipStatuses().
                        put(friendshipRows.getLong("from_id"), Friendship.CONFIRMED);
            } else {
                result.get(friendshipRows.getLong("to_id")).getFriendshipStatuses().
                        put(friendshipRows.getLong("from_id"), Friendship.UNCONFIRMED);
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
        SqlRowSet friendshipRows = jdbcTemplate.queryForRowSet("select * from FRIENDSHIPS where to_id = ?", id);
        while (friendshipRows.next()) {
            if (friendshipRows.getString("friendship_status").equals("CONFIRMED")) {
                user.getFriendshipStatuses().put(friendshipRows.getLong("from_id"), Friendship.CONFIRMED);
            } else {
                user.getFriendshipStatuses().put(friendshipRows.getLong("from_id"), Friendship.UNCONFIRMED);
            }
        }
        return user;
    }

    @Override
    public void addToFriends(long fromId, long toId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendships where from_id = ? and to_id = ?", toId, fromId);
        if (userRows.next()) {
            String sqlQuery = "insert into friendships (from_id, to_id, friendship_status)" +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    fromId,
                    toId,
                    "CONFIRMED");
            sqlQuery = "update friendships set " +
                    "friendship_status = ?" +
                    "where friendship_id = ?";
            jdbcTemplate.update(sqlQuery,
                    "CONFIRMED",
                    userRows.getLong("friendship_id"));
        } else {
            String sqlQuery = "insert into friendships (from_id, to_id, friendship_status)" +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    fromId,
                    toId,
                    "UNCONFIRMED");
        }
    }

    @Override
    public boolean isAlreadySendInvite(long fromId, long toId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendships where from_id = ? and to_id = ?", fromId, toId);
        return userRows.next();
    }

    @Override
    public void deleteInviteToFriend(long fromId, long toId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendships where from_id = ? and to_id = ?", toId, fromId);
        if (userRows.next()) {
            String sqlQuery = "update friendships set " +
                    "friendship_status = ?" +
                    "where friendship_id = ?";
            jdbcTemplate.update(sqlQuery,
                    "UNCONFIRMED",
                    userRows.getLong("friendship_id"));
        }
        String sqlQuery = "delete from friendships " +
                "where from_id = ? and to_id = ?";


        // на ноуте запрос удаления по ПК, а не по двум полям, при траблах проверить это место


        jdbcTemplate.update(sqlQuery, fromId, toId);
    }

    @Override
    public Set<Long> getAllFriends(long id) {
        Set<Long> friendsId = new HashSet<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendships where to_id = ?", id);
        while (userRows.next()) {
            long x = userRows.getLong("from_id");
            friendsId.add(x);
        }
        return friendsId;
    }
    @Override
    public Set<Long> friendsOfBothUsers (long firstId, long secondId) {
        Set<Long> friendsOfBoth = new HashSet<>();
        Set<Long> firstFriendsId = getAllFriends(firstId);
        Set<Long> secondFriendsId = getAllFriends(secondId);
        for (Long element : firstFriendsId) {
            if (secondFriendsId.contains(element)) {
                friendsOfBoth.add(element);
            }
        }
        return friendsOfBoth;
    }
    @Override
    public List<User> getUsersByIds(Set<Long> friends) {
        List<User> users = new ArrayList<>();
        Map<Long, User> allUsers = getAllUsers();
        for (Long element : friends) {
           users.add(allUsers.get(element));
        }
        return users;
    }
}
