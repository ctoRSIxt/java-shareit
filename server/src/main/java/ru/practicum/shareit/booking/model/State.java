package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exceptions.StateValidationException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;
}
