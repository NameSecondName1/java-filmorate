package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class User {
    long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    final Set<Long> friends = new HashSet<>();
    final Map<Long, Friendship> friendshipStatuses = new HashMap<>();
}
