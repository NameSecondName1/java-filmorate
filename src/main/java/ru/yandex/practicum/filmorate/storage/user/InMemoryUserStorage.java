package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage{
    private long globalId = 1;
    private final Map<Long, User> users = new HashMap<>();

/*    @Override
    public List<User> getAllUsers() {
        List<User> raspechatka = new ArrayList<>();
        if (users.size() != 0) {
            raspechatka.addAll(users.values());
        }
        return raspechatka;
    }*/
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
}
