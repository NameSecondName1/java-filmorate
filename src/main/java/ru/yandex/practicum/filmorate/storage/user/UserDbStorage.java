package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component ("userDbStorage")
public class UserDbStorage implements UserStorage{

    private long globalId = 1;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Map<Long, User> getAllUsers() {
        Map<Long, User> result = new HashMap<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from user");
        while (userRows.next()) {
            User user = new User(
                    userRows.getLong("id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate()
            );
            result.put(user.getId(), user);
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
    public User update(User user) {
        String sqlQuery = "update user set " +
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
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from user where id = ?", id);
        return userRows.next();
    }

    @Override
    public User getUserById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from user where id = ?", id);
        User user = new User(
                userRows.getLong("id"),
                userRows.getString("email"),
                userRows.getString("login"),
                userRows.getString("name"),
                userRows.getDate("birthday").toLocalDate()
        );
        return user;
    }

}
