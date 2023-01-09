package ru.yandex.practicum.filmorate.exception;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException() {
    }
    public UserDoesNotExistException (String message) {
        super(message);
    }
}
