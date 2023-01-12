package ru.yandex.practicum.filmorate.exception;

public class NoLikeException extends RuntimeException{
    public NoLikeException () {
    }
    public NoLikeException (String message) {
        super(message);
    }
}
