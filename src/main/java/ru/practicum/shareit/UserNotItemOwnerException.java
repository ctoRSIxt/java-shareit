package ru.practicum.shareit;

public class UserNotItemOwnerException extends RuntimeException{

    public UserNotItemOwnerException(String message) {
        super(message);
    }
}
