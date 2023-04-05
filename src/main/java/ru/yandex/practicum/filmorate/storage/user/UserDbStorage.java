package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, User> getAllUsers() {
        Map<Long, User> result = new HashMap<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS AS U ORDER BY U.ID ASC");
      //  SqlRowSet friendshipRows = jdbcTemplate.queryForRowSet("select * from FRIENDSHIPS");

        while (userRows.next()) {
            User user = new User(userRows.getLong("id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());
            result.put(user.getId(), user);
        }


/*        while (friendshipRows.next()) {
            if (friendshipRows.getString("friendship_status").equals("CONFIRMED")) {
                result.get(friendshipRows.getLong("user_id")).getFriendshipStatuses().
                        put(friendshipRows.getLong("friend_id"), Friendship.CONFIRMED);
            } else {
                result.get(friendshipRows.getLong("user_id")).getFriendshipStatuses().
                        put(friendshipRows.getLong("friend_id"), Friendship.UNCONFIRMED);
            }
        }*/

        return result;
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO users (name, login, email, birthday) " +
                    "VALUES (?, ?, ?, ?)", new String[] {"id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getEmail());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        long generatedId = keyHolder.getKey().longValue();

        user.setId(generatedId);
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
   /*     SqlRowSet friendshipRows = jdbcTemplate.queryForRowSet("select * from FRIENDSHIPS where user_id = ?", id);
        while (friendshipRows.next()) {
            if (friendshipRows.getString("friendship_status").equals("CONFIRMED")) {
                user.getFriendshipStatuses().put(friendshipRows.getLong("friend_id"), Friendship.CONFIRMED);
            } else {
                user.getFriendshipStatuses().put(friendshipRows.getLong("friend_id"), Friendship.UNCONFIRMED);
            }
        }*/
        return user;
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
