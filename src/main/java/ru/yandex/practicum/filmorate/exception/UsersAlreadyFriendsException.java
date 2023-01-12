package ru.yandex.practicum.filmorate.exception;

public class UsersAlreadyFriendsException extends RuntimeException{
    public UsersAlreadyFriendsException () {
    }
    public UsersAlreadyFriendsException (String message) {
        super(message);
    }
}
