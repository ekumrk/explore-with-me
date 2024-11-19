package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.request.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.model.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final RequestService service;

    @RequestMapping(value = "/users/{userId}/requests", method = RequestMethod.GET)
    public List<ParticipationRequestDto> getUserRequests(@PathVariable @Positive long userId) {
        return service.getUserRequests(userId);
    }

    @RequestMapping(value = "/users/{userId}/requests", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addNewRequest(@PathVariable @Positive long userId,
                                                 @RequestParam @Positive long eventId) {
        return service.addNewRequest(userId, eventId);
    }

    @RequestMapping(value = "/users/{userId}/requests/{requestId}/cancel", method = RequestMethod.PATCH)
    public ParticipationRequestDto cancelRequest(@PathVariable @Positive long userId,
                                                 @PathVariable @Positive long requestId) {
        return service.cancelRequest(userId, requestId);
    }

    @RequestMapping(value = "/users/{userId}/events/{eventId}/requests", method = RequestMethod.PATCH)
    public EventRequestStatusUpdateResult updateEventRequestPrivate(@PathVariable @Positive long userId,
                                                                    @PathVariable @Positive long eventId,
                                                                    @RequestBody EventRequestStatusUpdateRequest dto) {
        return service.updateEventRequestPrivate(userId, eventId, dto);
    }

    @RequestMapping(value = "/users/{userId}/events/{eventId}/requests", method = RequestMethod.GET)
    public List<ParticipationRequestDto> getUserEventRequestPrivate(@PathVariable @Positive long userId,
                                                                    @PathVariable @Positive long eventId) {
        return service.getUserEventRequestPrivate(userId, eventId);
    }
}
