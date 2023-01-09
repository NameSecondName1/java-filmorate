package ru.yandex.practicum.filmorate.exception;

public class UsersNotFriendsException extends Exception{
    public UsersNotFriendsException () {
    }
    public UsersNotFriendsException (String message) {
        super(message);
    }
}
