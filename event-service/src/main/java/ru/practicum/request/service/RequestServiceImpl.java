package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestMapper;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.model.dto.ParticipationRequestDto;
import ru.practicum.request.repository.RequestsRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {

    private final RequestsRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper mapper;

    @Override
    public ParticipationRequestDto addNewRequest(Long userId, Long eventId) throws NullPointerException {
        LocalDateTime created = LocalDateTime.now();
        Optional<Request> previous = repository.findFirstByEventIdAndRequesterId(eventId, userId);

        if (previous.isPresent()) {
            throw new ConflictException("Request already exists");
        }

        Event event = checkIfEventExists(eventId);

        if (event.getInitiator().getId() == userId) {
            throw new ConflictException("User cannot request participation on his own events");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event is not published");
        }

        int limit = repository.findAllByEventId(eventId).size();

        if (limit != 0 && event.getParticipantLimit() != 0 && limit >= event.getParticipantLimit()) {
            throw new ConflictException("Cannot create request, reached limit of participants");
        }

        User requester = checkIfUserExist(userId);
        Request req = Request.builder()
                .event(event)
                .requester(requester)
                .created(created)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            req.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            req.setStatus(RequestStatus.PENDING);
        }

        return mapper.mapRequestToParticipationRequestDto(repository.saveAndFlush(req));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        checkIfUserExist(userId);
        Request req = repository.findById(requestId).orElseThrow(
                () -> new EntityNotFoundException("Request does not exist")
        );

        if (req.getRequester().getId() != userId) {
            throw new BadRequestException("User is not the requestor");
        }

        if (req.getStatus().equals(RequestStatus.CONFIRMED)) {
            req.getEvent().setConfirmedRequests(
                    req.getEvent().getConfirmedRequests() - 1
            );
            eventRepository.save(req.getEvent());
        }

        req.setStatus(RequestStatus.CANCELED);
        return mapper.mapRequestToParticipationRequestDto(
                repository.saveAndFlush(req)
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        checkIfUserExist(userId);
        return repository.findAllByRequesterId(userId).stream()
                .map(mapper::mapRequestToParticipationRequestDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestPrivate(Long userId, Long eventId,
                                                                    EventRequestStatusUpdateRequest dto) {
        checkIfUserExist(userId);
        Event event = checkIfEventExists(eventId);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0 || dto.getRequestIds().isEmpty()) {
            return new EventRequestStatusUpdateResult(new ArrayList<>(), new ArrayList<>());
        }

        List<Request> requests = repository.findAllByIdIn(dto.getRequestIds());

        if (!requests.stream()
                .map(Request::getStatus)
                .allMatch(RequestStatus.PENDING::equals)) {
            throw new ConflictException("All requests must be PENDING");
        }

        if (requests.size() != dto.getRequestIds().size()) {
            throw new ConflictException("Some requests are lost");
        }

        List<Request> confirmedList = new ArrayList<>();
        List<Request> rejectedList = new ArrayList<>();

        if (dto.getStatus() != null && dto.getStatus().equals(RequestStatus.REJECTED)) {
            rejectedList.addAll(requests.stream()
                    .peek(request -> request.setStatus(RequestStatus.REJECTED))
                    .collect(Collectors.toUnmodifiableList()));
            repository.saveAllAndFlush(rejectedList);
        } else {
            long newConfirmedRequests = repository.findAllByEventIdAndStatusConfirmed(eventId) +
                    dto.getRequestIds().size();

            if (newConfirmedRequests > event.getParticipantLimit()) {
                rejectedList.addAll(repository.findAllByEventIdAndStatus(eventId, RequestStatus.PENDING).stream()
                        .peek(request -> request.setStatus(RequestStatus.REJECTED))
                        .collect(Collectors.toUnmodifiableList()));
                repository.saveAllAndFlush(rejectedList);
            }

            confirmedList.addAll(requests.stream()
                    .peek(request -> request.setStatus(RequestStatus.CONFIRMED))
                    .collect(Collectors.toUnmodifiableList()));
            repository.saveAllAndFlush(confirmedList);
            event.setConfirmedRequests(newConfirmedRequests);
            eventRepository.save(event);
        }

        return new EventRequestStatusUpdateResult(confirmedList.stream()
                .map(mapper::mapRequestToParticipationRequestDto)
                .collect(Collectors.toUnmodifiableList()), rejectedList.stream()
                .map(mapper::mapRequestToParticipationRequestDto)
                .collect(Collectors.toUnmodifiableList()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserEventRequestPrivate(Long userId, Long eventId) {
        checkIfUserExist(userId);
        checkIfEventExists(eventId);
        List<Request> result = repository.findAllByEventId(eventId);

        if (result.isEmpty()) {
            return new ArrayList<>();
        } else {
            return result.stream()
                    .map(mapper::mapRequestToParticipationRequestDto)
                    .collect(Collectors.toUnmodifiableList());
        }
    }

    private User checkIfUserExist(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
    }

    private Event checkIfEventExists(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Event not found")
        );
    }
}
