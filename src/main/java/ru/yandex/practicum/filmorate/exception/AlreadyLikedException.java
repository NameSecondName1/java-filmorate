package ru.yandex.practicum.filmorate.exception;

public class AlreadyLikedException extends Exception{
    public AlreadyLikedException () {
    }
    public AlreadyLikedException (String message) {
        super(message);
    }
}
