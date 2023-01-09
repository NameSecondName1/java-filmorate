package ru.yandex.practicum.filmorate.exception;

public class UsersAlreadyFriendsException extends Exception{
    public UsersAlreadyFriendsException () {
    }
    public UsersAlreadyFriendsException (String message) {
        super(message);
    }
}
