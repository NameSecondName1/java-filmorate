package ru.yandex.practicum.filmorate.exception;

public class EntityNotFountException  extends RuntimeException {
    Object x;
    public EntityNotFountException() {
    }
    public EntityNotFountException (String message) {
        super(message);
    }

    public EntityNotFountException (String message, Object x) {
        super(message);
    }
}
