package ru.yandex.practicum.filmorate.exception;

public class EntityNotFountException  extends RuntimeException {
    public EntityNotFountException() {
    }
    public EntityNotFountException (String message) {
        super(message);
    }

}
