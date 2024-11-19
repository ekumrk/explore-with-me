package ru.practicum.request.model;

import ru.practicum.exception.BadRequestException;

public enum RequestStatus {
    PENDING,
    REJECTED,
    CONFIRMED,
    CANCELED;

    public static void checkRequestStatus(String status) {
        int isPresent = 0;
        for (RequestStatus value : RequestStatus.values()) {
            if (value.name().equals(status)) {
                isPresent++;
            }
        }
        if (isPresent != 1) {
            throw new BadRequestException("Unknown request status: " + status);
        }
    }
}
