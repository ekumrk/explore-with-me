package ru.practicum.event.model;

import ru.practicum.exception.BadRequestException;

public enum EventState {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static void checkEventState(String state) {
        int isPresent = 0;
        for (EventState value : EventState.values()) {
            if (value.name().equals(state)) {
                isPresent++;
            }
        }
        if (isPresent != 1) {
            throw new BadRequestException("Unknown event state: " + state);
        }
    }
}
