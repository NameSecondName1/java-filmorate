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

    private long globalId = 4;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Map<Long, User> getAllUsers() {

        //     SELECT * FROM PUBLIC."user" u
        //     LEFT OUTER join "friendship" AS f ON u.id = f.to_id
        //
        //     В БОБРЕ ТАК


        Map<Long, User> result = new HashMap<>();
        User user = new User(0, null, null, null, null);
        Map<Long, Friendship> friendships = new HashMap<>();
        long firstId = 1;

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from user AS u" +
                "left outer join friendship AS f ON u.id = f.to_id" +
                "order by id asc");

        while (userRows.next()) {
            if (userRows.getLong("id") != firstId) {
                firstId = userRows.getLong("id");
                user.setFriendshipStatuses(friendships);
                result.put(user.getId(), user);
                friendships.clear();
            }
            if (userRows.getObject("from_id", Long.class) != null) {
                friendships.put(userRows.getLong("from_id"),
                        userRows.getObject("friendship_status", Friendship.class));
            }
            user.setId(userRows.getLong("id"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setName(userRows.getString("name"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());
        }

        return result;
    }

    @Override
    public User create(User user) {
        user.setId(globalId);
        globalId++;
        String sqlQuery = "insert into user(id, name, login, email, birthday) " +
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
    public User update(User user) {         // CHANGED
        String sqlQuery = "update user set " +
                "name = ?, login = ?, email = ?, birthday = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
       /* updateFriendship(user.getFriendshipStatuses(), user.getId());*/
        return user;
    }
/*
    private void updateFriendship(Map<Long, Friendship> friendships, long id) {
        // по аналогии с жанрами у фильма


    }
*/


    @Override
    public boolean isContainId(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from user where id = ?", id);
        return userRows.next();
    }

    @Override
    public User getUserById(long id) {      // CHANGED
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from user AS u" +
                "left outer join friendship AS f ON u.id = f.to_id" +
                "where u.id = ?", id);

        User user = new User(userRows.getLong("id"),
                userRows.getString("email"),
                userRows.getString("login"),
                userRows.getString("name"),
                userRows.getDate("birthday").toLocalDate());
        Map<Long, Friendship> friendships = new HashMap<>();
        while (userRows.next()) {
            if (userRows.getObject("from_id", Long.class) != null) {
                friendships.put(userRows.getLong("from_id"),
                        userRows.getObject("friendship_status", Friendship.class));
            }
        }
        user.setFriendshipStatuses(friendships);
        return user;
    }

    @Override
    public void addToFriends(long fromId, long toId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendship where from_id = ? and to_id = ?", toId, fromId);
        if (userRows.next()) {
            String sqlQuery = "insert into friendship (from_id, to_id, friendship_status) " +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    fromId,
                    toId,
                    Friendship.CONFIRMED);
            sqlQuery = "update friendship set " +
                    "friendship_status = ?" +
                    "where friendship_id = ?";
            jdbcTemplate.update(sqlQuery,
                    Friendship.CONFIRMED,
                    userRows.getLong("friendship_id"));
        } else {
            String sqlQuery = "insert into friendship (from_id, to_id, friendship_status) " +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    fromId,
                    toId,
                    Friendship.UNCONFIRMED);
        }
    }

    @Override
    public boolean isAlreadySendInvite(long fromId, long toId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendship where from_id = ? and to_id = ?", fromId, toId);
        return userRows.next();
    }

    @Override
    public void deleteInviteToFriend(long fromId, long toId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendship where from_id = ? and to_id = ?", toId, fromId);
        if (userRows.next()) {
            String sqlQuery = "update friendship set " +
                    "friendship_status = ?" +
                    "where friendship_id = ?";
            jdbcTemplate.update(sqlQuery,
                    Friendship.UNCONFIRMED,
                    userRows.getLong("friendship_id"));
        }
        String sqlQuery = "delete from friendship" +
                "where from_id = ? and to_id = ?";


        // на ноуте запрос удаления по ПК, а не по двум полям, при траблах проверить это место


        jdbcTemplate.update(sqlQuery, fromId, toId);
    }

    @Override
    public Set<Long> getAllFriends(long id) {
        Set<Long> friendsId = new HashSet<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendship where to_id = ?", id);
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
