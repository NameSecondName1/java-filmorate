package ru.yandex.practicum.filmorate.exception;

public class NoLikeException extends Exception{
    public NoLikeException () {
    }
    public NoLikeException (String message) {
        super(message);
    }
}
