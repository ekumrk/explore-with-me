package ru.practicum.request.model;

import ru.practicum.exception.BadRequestException;

public enum StateAction {
    PUBLISH_EVENT,
    REJECT_EVENT,
    SEND_TO_REVIEW,
    CANCEL_REVIEW;

    public static void checkStateAction(String stateAction) {
        int isPresent = 0;
        for (StateAction value : StateAction.values()) {
            if (value.name().equals(stateAction)) {
                isPresent++;
            }
        }
        if (isPresent != 1) {
            throw new BadRequestException("Unknown StateAction: " + stateAction);
        }
    }
}
