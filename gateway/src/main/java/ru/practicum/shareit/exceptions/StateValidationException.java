package ru.practicum.shareit.exceptions;

public class StateValidationException extends RuntimeException {
    public StateValidationException(String message) {
        super(message);
    }
}
