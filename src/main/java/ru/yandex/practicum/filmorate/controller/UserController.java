package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping({"/users"})
@Slf4j

public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
       return userService.getAllUsers();
    }

    @PostMapping
    public User create(@RequestBody User user){
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user){
       return userService.update(user);
    }

    @PutMapping("/{fromId}/friends/{toId}")
    public void addToFriends (@PathVariable long fromId, @PathVariable long toId) {
            userService.addToFriends(fromId, toId);
    }

    @DeleteMapping("/{fromId}/friends/{toId}")
    public void deleteInviteToFriend(@PathVariable long fromId, @PathVariable long toId) {
            userService.deleteInviteToFriend(fromId, toId);
    }

    @GetMapping("/{id}/friends")
    public Set<Long> getAllFriends (@PathVariable long id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<Long> getFriendsOfBothUsers (@PathVariable long id, @PathVariable long otherId) {
        return userService.friendsOfBothUsers(id, otherId);
    }

    @GetMapping("/{id}")
    public User getUserById (@PathVariable long id) {
        return userService.getUserById(id);
    }

}
