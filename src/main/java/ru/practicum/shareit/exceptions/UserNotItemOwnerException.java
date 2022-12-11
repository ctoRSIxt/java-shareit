package ru.practicum.shareit.exceptions;

public class UserNotItemOwnerException extends RuntimeException{

    public UserNotItemOwnerException(String message) {
        super(message);
    }
}
