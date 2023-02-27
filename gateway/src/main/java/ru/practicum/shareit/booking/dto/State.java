package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exceptions.StateValidationException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State validateState(String stateString) {
        State state = null;
        try {
            state = State.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new StateValidationException("Unknown state: " + stateString);
        }
        return state;
    }
}
