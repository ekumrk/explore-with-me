package ru.practicum.request.service;

import ru.practicum.request.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.model.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto addNewRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getUserRequests(Long userId);

    EventRequestStatusUpdateResult updateEventRequestPrivate(Long userId, Long eventId,
                                                             EventRequestStatusUpdateRequest dto);

    List<ParticipationRequestDto> getUserEventRequestPrivate(Long userId, Long eventId);
}
