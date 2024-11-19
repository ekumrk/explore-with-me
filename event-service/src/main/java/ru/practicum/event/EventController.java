package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.EventShortDto;
import ru.practicum.event.model.dto.NewEventDto;
import ru.practicum.event.service.EventService;
import ru.practicum.event.model.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.dto.UpdateEventUserRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constants.Constants.DATE_TIME_FORMAT;
import static ru.practicum.constants.Constants.FROM;
import static ru.practicum.constants.Constants.SIZE;

@RestController
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService service;

    @RequestMapping(value = "/users/{userId}/events", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addNewEventPrivate(@PathVariable @Positive long userId,
                                           @RequestBody @Valid NewEventDto dto) {
        return service.addNewEventPrivate(userId, dto);
    }

    @RequestMapping(value = "/users/{userId}/events/{eventId}", method = RequestMethod.PATCH)
    public EventFullDto updateUserEventPrivate(@PathVariable @Positive long userId,
                                               @PathVariable @Positive long eventId,
                                               @RequestBody @Valid UpdateEventUserRequest dto) {
        return service.updateUserEventPrivate(userId, eventId, dto);
    }

    @RequestMapping(value = "/users/{userId}/events", method = RequestMethod.GET)
    public List<EventShortDto> getUserEventsPrivate(@PathVariable long userId,
                                                    @RequestParam(defaultValue = FROM) @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = SIZE) @Positive int size) {
        return service.getUserEventsPrivate(userId, from, size);
    }

    @RequestMapping(value = "/users/{userId}/events/{eventId}", method = RequestMethod.GET)
    public EventFullDto getUserEventPrivate(@PathVariable @Positive long userId,
                                            @PathVariable @Positive long eventId) {
        return service.getUserEventPrivate(userId, eventId);
    }

    @RequestMapping(value = "/admin/events/{eventId}", method = RequestMethod.PATCH)
    public EventFullDto updateEventAdmin(@PathVariable @Positive long eventId,
                                         @RequestBody @Valid UpdateEventAdminRequest dto) {
        return service.updateEventAdmin(eventId, dto);
    }

    @RequestMapping(value = "/admin/events", method = RequestMethod.GET)
    public List<EventFullDto> getEventsAdmin(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<EventState> states,
                                             @RequestParam(required = false) List<Integer> categories,
                                             @RequestParam(required = false) @DateTimeFormat(fallbackPatterns = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                             @RequestParam(required = false) @DateTimeFormat(fallbackPatterns = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                             @RequestParam(defaultValue = FROM) @PositiveOrZero int from,
                                             @RequestParam(defaultValue = SIZE) @Positive int size) {
        return service.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public List<EventShortDto> getEventsPublic(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) List<Integer> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false) @DateTimeFormat(fallbackPatterns = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                               @RequestParam(required = false) @DateTimeFormat(fallbackPatterns = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                               @RequestParam(required = false) boolean onlyAvailable,
                                               @RequestParam(required = false) String sort,
                                               @RequestParam(defaultValue = FROM) @PositiveOrZero int from,
                                               @RequestParam(defaultValue = SIZE) @Positive int size,
                                               HttpServletRequest request) {
        return service.getEventsPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request);
    }

    @RequestMapping(value = "/events/{id}", method = RequestMethod.GET)
    public EventFullDto getEventPublic(@PathVariable @Positive long id, HttpServletRequest request) {
        return service.getEventPublic(id, request);
    }
}
