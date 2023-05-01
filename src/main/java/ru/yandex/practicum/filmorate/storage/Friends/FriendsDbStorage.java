package ru.yandex.practicum.filmorate.storage.Friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserRowMapper;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Component
public class FriendsDbStorage implements FriendsStorage{
    final UserRowMapper userRowMapper;

    private final JdbcTemplate jdbcTemplate;

    public FriendsDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public void addToFriends(long userId, long friendId) {
        String sqlQuery = "insert into friendships (user_id, friend_id)" +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                userId,
                friendId);
    }

    @Override
    public boolean isAlreadyFriend(long userId, long friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendships where user_id = ? and friend_id = ?", userId, friendId);
        return userRows.next();
    }

    @Override
    public void deleteFromFriend(long userId, long friendId) {
        String sqlQuery = "delete from friendships " +
                "where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getAllFriends(long id) {
        String query = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM users u " +
                "INNER JOIN friendships f ON u.id = f.friend_id " +
                "WHERE f.user_id = ?";
        List<User> friends = jdbcTemplate.query(query, userRowMapper, id);
        return friends;
    }

    @Override
    public List<User> friendsOfBothUsers(long firstId, long secondId) {

        String sql = "select * from USERS AS U, FRIENDSHIPS AS F, FRIENDSHIPS AS O " +
                "where U.id = F.friend_id AND U.id = O.friend_id AND " +
                "F.USER_ID = ? AND O.USER_ID = ?";
        Object[] args = new Object[] { firstId, secondId };
        int[] argTypes = new int[] { Types.BIGINT, Types.BIGINT }; // каждому типу данных соответствует целое число, в данном случае два одинаковых числа
        List<User> friendsOfBoth = jdbcTemplate.query(sql, args, argTypes, userRowMapper);
        return friendsOfBoth;
    }
}
