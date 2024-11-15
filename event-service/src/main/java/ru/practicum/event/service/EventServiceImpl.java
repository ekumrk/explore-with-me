package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.client.StatisticsClient;
import ru.practicum.dto.InputEventDto;
import ru.practicum.dto.OutputStatsDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventMapper;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.EventShortDto;
import ru.practicum.event.model.dto.NewEventDto;
import ru.practicum.event.model.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.dto.UpdateEventUserRequest;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.location.model.Location;
import ru.practicum.location.model.dto.LocationDto;
import ru.practicum.location.service.LocationService;
import ru.practicum.request.repository.RequestsRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.constants.Constants.DATE_TIME_FORMAT;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    private final UserRepository userRepository;
    private final RequestsRepository requestRepository;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final EventMapper mapper;
    private final StatisticsClient statsClient;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    @Value(value = "${app.name}")
    private String appName;

    @Transactional
    @Override
    public EventFullDto addNewEventPrivate(long userId, NewEventDto dto) {
        checkEventDate(dto.getEventDate(), false);
        LocalDateTime createdOn = LocalDateTime.now();
        Location loc = locationService.addNewLocation(dto.getLocation());
        User initiator = checkIfUserExists(userId);
        Event event = mapper.mapNewEventDtoToEvent(dto);
        Category cat = categoryService.getCategory(dto.getCategory());
        event.setCategory(cat);
        event.setInitiator(initiator);
        event.setLocation(loc);
        event.setCreatedOn(createdOn);
        event.setState(EventState.PENDING);

        if (dto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        return mapper.mapEventToEventFullDto(
                repository.saveAndFlush(event)
        );
    }

    @Transactional
    @Override
    public EventFullDto updateUserEventPrivate(long userId, long eventId, UpdateEventUserRequest dto) {
        if (dto.getEventDate() != null) {
            checkEventDate(dto.getEventDate(), false);
        }

        Event event = checkIfEventExists(eventId);
        Event update = mapper.mapUpdateEventUserRequestToEvent(dto);
        setCategoryAndLocation(event, dto.getCategory(), dto.getLocation());
        updateEventFields(event, update);

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        return mapper.mapEventToEventFullDto(repository.saveAndFlush(event));
    }

    @Override
    public List<EventShortDto> getUserEventsPrivate(long userId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
        checkIfUserExists(userId);
        List<Event> list = repository.findAllByInitiatorId(userId, page);
        return list.isEmpty() ? new ArrayList<>() : list.stream()
                .peek(event -> event.setConfirmedRequests(getConfirmedRequests(event.getId())))
                .map(mapper::mapEventToEventShortDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public EventFullDto getUserEventPrivate(long userId, long eventId) {
        checkIfUserExists(userId);
        Event event = checkIfEventExists(eventId);
        event.setConfirmedRequests(getConfirmedRequests(eventId));

        if (event.getInitiator().getId() != userId) {
            throw new ConflictException("User is not initiator of event");
        }

        return mapper.mapEventToEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateEventAdmin(long eventId, UpdateEventAdminRequest dto) {
        if (dto.getEventDate() != null) {
            checkEventDate(dto.getEventDate(), true);
        }

        Event event = checkIfEventExists(eventId);
        Event update = mapper.mapUpdateEventAdminRequestToEvent(dto);
        setCategoryAndLocation(event, dto.getCategory(), dto.getLocation());
        updateEventFields(event, update);

        if (dto.getStateAction() != null) {
            if (!event.getState().equals(EventState.PENDING)) {
                throw new ConflictException("Event is not pending");
            }

            switch (dto.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        return mapper.mapEventToEventFullDto(repository.saveAndFlush(event));
    }

    @Override
    public List<EventFullDto> getEventsAdmin(List<Long> users, List<EventState> states, List<Integer> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
        LocalDateTime start = rangeStart == null ? LocalDateTime.now() : rangeStart;
        LocalDateTime end = rangeEnd == null ? LocalDateTime.now().plusYears(100L) : rangeEnd;

        if (end.isBefore(start)) {
            throw new BadRequestException("Invalid start/end time");
        }

        List<Event> list = repository.findAllByAdmin(users, states, categories, start, end, page);
        return list.stream()
                .peek(event -> event.setConfirmedRequests(getConfirmedRequests(event.getId())))
                .map(mapper::mapEventToEventFullDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Integer> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable,
                                               String sort, int from, int size, HttpServletRequest request) {
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
        LocalDateTime start = rangeStart == null ? LocalDateTime.now() : rangeStart;
        LocalDateTime end = rangeEnd == null ? LocalDateTime.now().plusYears(100L) : rangeEnd;
        statsClient.save(InputEventDto.builder()
                .app(appName)
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .requestDate(LocalDateTime.now())
                .build());

        if (categories != null && categories.size() == 1 && categories.get(0).equals(0)) {
            categories = null;
        }

        if (end.isBefore(start)) {
            throw new BadRequestException("Invalid start/end time");
        }

        List<Event> events = text == null ? repository.findAll()
                : repository.findAllByPublic(text.toLowerCase(), categories, paid, start, end, page);

        if (onlyAvailable) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() == 0
                            || event.getParticipantLimit() < requestRepository.findAllByEventIdAndStatusConfirmed(event.getId()))
                    .collect(Collectors.toUnmodifiableList());
        }

        List<String> eventsUrl = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toUnmodifiableList());
        List<OutputStatsDto> stats = statsClient.getStats(start.format(formatter), end.format(formatter), eventsUrl, true);
        List<EventShortDto> shortDtos = new ArrayList<>(
                events.stream()
                        .map(mapper::mapEventToEventShortDto)
                        .peek(eventShortDto -> {
                            java.util.Optional<OutputStatsDto> tmp = stats.stream()
                                    .filter(outputStatsDto -> outputStatsDto.getUri().equals("/events/" + eventShortDto.getId()))
                                    .findFirst();
                            eventShortDto.setViews(tmp.map(OutputStatsDto::getHits).orElse(0L));
                        })
                        .peek(eventShortDto -> eventShortDto.setConfirmedRequests(getConfirmedRequests(eventShortDto.getId())))
                        .collect(Collectors.toUnmodifiableList())
        );

        if (sort != null) {
            switch (sort) {
                case "EVENT_DATE":
                    shortDtos.sort(Comparator.comparing(EventShortDto::getEventDate));
                    break;
                case "VIEWS":
                    shortDtos.sort(Comparator.comparing(EventShortDto::getViews).reversed());
                    break;
            }
        }

        if (from >= shortDtos.size()) {
            return new ArrayList<>();
        }

        int toIndex = Math.min(from + size, shortDtos.size());
        return shortDtos.subList(from, toIndex);
    }

    @Override
    public EventFullDto getEventPublic(long id, HttpServletRequest request) {
        statsClient.save(InputEventDto.builder()
                .app(appName)
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .requestDate(LocalDateTime.now())
                .build());
        Event event = checkIfEventExists(id);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EntityNotFoundException("Event with id=" + id + " was not published");
        }

        List<OutputStatsDto> stats = statsClient.getStats(event.getPublishedOn().format(formatter),
                LocalDateTime.now().plusYears(100).format(formatter), List.of(request.getRequestURI()), true);
        EventFullDto dto = mapper.mapEventToEventFullDto(event);
        dto.setViews(stats.isEmpty() ? 0L : stats.get(0).getHits());
        dto.setConfirmedRequests(getConfirmedRequests(dto.getId()));
        return dto;
    }

    private Event checkIfEventExists(Long eventId) {
        return repository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Event with id=" + eventId + " was not found")
        );
    }

    private User checkIfUserExists(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id=" + userId + " was not found")
        );
    }

    private void checkEventDate(LocalDateTime eventDate, boolean admin) {
        if (!admin) {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Event can't be in less than 2 hours!");
            }
        } else {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new BadRequestException("Event can't be in less than 1 hour!");
            }
        }
    }

    private long getConfirmedRequests(Long eventId) {
        return requestRepository.findAllByEventIdAndStatusConfirmed(eventId);
    }

    private void updateEventFields(Event recepient, Event donor) {
        if (recepient.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("You cannot change published event!");
        }

        if (donor.getAnnotation() != null && !donor.getAnnotation().isBlank()) {
            recepient.setAnnotation(donor.getAnnotation());
        }

        if (donor.getDescription() != null && !donor.getDescription().isBlank()) {
            recepient.setDescription(donor.getDescription());
        }

        if (donor.getEventDate() != null) {
            recepient.setEventDate(donor.getEventDate());
        }

        if (donor.getPaid() != null) {
            recepient.setPaid(donor.getPaid());
        }

        if (donor.getParticipantLimit() != 0) {
            recepient.setParticipantLimit(donor.getParticipantLimit());
        }

        if (donor.getRequestModeration() != null) {
            recepient.setRequestModeration(donor.getRequestModeration());
        }

        if (donor.getTitle() != null && !donor.getTitle().isBlank()) {
            recepient.setTitle(donor.getTitle());
        }
    }

    private void setCategoryAndLocation(Event event, int categoryId, LocationDto location) {
        if (categoryId != 0) {
            event.setCategory(categoryService.getCategory(categoryId));
        }

        if (location != null) {
            Location loc = locationService.addNewLocation(location);
            event.setLocation(loc);
        }
    }
}
