package ru.yandex.practicum.filmorate.model;

import lombok.Builder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



public class User {
    private long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
  //  private Map<Long, Friendship> friendshipStatuses = new HashMap<>();

    public User(long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

/*    public Map<Long, Friendship> getFriendshipStatuses() {
        return friendshipStatuses;
    }

    public void setFriendshipStatuses(Map<Long, Friendship> friendshipStatuses) {
        this.friendshipStatuses = friendshipStatuses;
    }*/
}
