package ru.yandex.practicum.filmorate.exception;

public class UsersNotFriendsException extends RuntimeException{
    public UsersNotFriendsException () {
    }
    public UsersNotFriendsException (String message) {
        super(message);
    }
}
