package ru.practicum.event.service;

import ru.practicum.event.model.EventState;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.EventShortDto;
import ru.practicum.event.model.dto.NewEventDto;
import ru.practicum.event.model.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.dto.UpdateEventUserRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addNewEventPrivate(long userId, NewEventDto dto);

    EventFullDto updateUserEventPrivate(long userId, long eventId, UpdateEventUserRequest dto);

    List<EventShortDto> getUserEventsPrivate(long userId, int from, int size);

    EventFullDto getUserEventPrivate(long userId, long eventId);

    EventFullDto updateEventAdmin(long eventId, UpdateEventAdminRequest dto);

    List<EventFullDto> getEventsAdmin(List<Long> users, List<EventState> states, List<Integer> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    List<EventShortDto> getEventsPublic(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, boolean onlyAvailable, String sort,
                                        int from, int size, HttpServletRequest request);

    EventFullDto getEventPublic(long id, HttpServletRequest request);
}
