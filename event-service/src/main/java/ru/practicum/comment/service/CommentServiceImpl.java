package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentMapper;
import ru.practicum.comment.model.dto.CommentDto;
import ru.practicum.comment.model.dto.NewCommentDto;
import ru.practicum.comment.model.dto.ShortCommentDto;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestsRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestsRepository requestsRepository;
    private final CommentMapper mapper;

    @Override
    public CommentDto addNewComment(Long userId, Long eventId, NewCommentDto dto) {
        Comment comment = mapper.mapNewCommentDtoToComment(dto);
        comment.setCreated(LocalDateTime.now());
        User author = checkIfUserExists(userId);
        Event event = checkIfEventExists(eventId);
        if (event.getInitiator().getId() != userId) {
            participantCheck(eventId, userId);
        }
        comment.setAuthor(author);
        comment.setEvent(event);
        return mapper.mapCommentToCommentDto(
                repository.saveAndFlush(comment)
        );
    }

    @Override
    public ShortCommentDto update(Long userId, Long eventId, Long commentId, NewCommentDto dto) {
        checkIfUserExists(userId);
        checkIfEventExists(eventId);
        Comment comment = checkIfCommentExists(commentId);
        updateCheck(comment.getAuthor().getId(), userId, comment.getEvent().getId(), eventId);
        comment.setText(dto.getText());
        return mapper.mapCommentToShortCommentDto(
                repository.saveAndFlush(comment)
        );
    }

    @Override
    public CommentDto adminUpdate(Long eventId, Long commentId, CommentDto dto) {
        checkIfEventExists(eventId);
        Comment comment = checkIfCommentExists(commentId);
        updateCheck(null, null, comment.getEvent().getId(), eventId);
        comment.setText(dto.getText());
        return mapper.mapCommentToCommentDto(
                repository.saveAndFlush(comment)
        );
    }

    @Override
    public void delete(Long userId, Long commentId) {
        checkIfUserExists(userId);
        Comment com = checkIfCommentExists(commentId);

        if (com.getAuthor().getId() != userId) {
            throw new ConflictException("Only author can delete comment");
        }

        repository.deleteById(commentId);
        repository.flush();
    }

    @Override
    public void adminDelete(Long userId, Long commentId) {
        checkIfUserExists(userId);
        checkIfCommentExists(commentId);
        repository.deleteById(commentId);
        repository.flush();
    }

    @Override
    public List<ShortCommentDto> getEventComments(Long eventId) {
        checkIfEventExists(eventId);
        return repository.findAllByEventId(eventId).stream()
                .map(mapper::mapCommentToShortCommentDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ShortCommentDto> getEventUserComments(Long userId, Long eventId) {
        checkIfUserExists(userId);
        checkIfEventExists(eventId);
        return repository.findAllByEventIdAndAuthorId(eventId, userId).stream()
                .map(mapper::mapCommentToShortCommentDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<CommentDto> getAdminEventComments(Long eventId) {
        checkIfEventExists(eventId);
        return repository.findAllByEventId(eventId).stream()
                .map(mapper::mapCommentToCommentDto)
                .collect(Collectors.toUnmodifiableList());
    }

    private Comment checkIfCommentExists(Long commentId) {
        return repository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Comment with id=" + commentId + " was not found")
        );
    }

    private User checkIfUserExists(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id=" + userId + " was not found")
        );
    }

    private Event checkIfEventExists(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Event with id=" + eventId + " was not found")
        );
    }

    private void participantCheck(Long eventId, Long userId) {
        Optional<Request> request = requestsRepository.findFirstByEventIdAndRequesterId(eventId, userId);
        if (request.isPresent()) {
            if (request.get().getRequester().getId() != userId &&
                    (!request.get().getStatus().equals(RequestStatus.CONFIRMED)
                            || !request.get().getStatus().equals(RequestStatus.PENDING))) {
                throw new ConflictException("Only participants can leave comments");
            }
        } else {
            throw new ConflictException("Author did not send request to participate the event");
        }
    }

    private void updateCheck(Long authorId, Long userId, Long eventId, Long inputEventId) {
        if (authorId != null && userId != null && !Objects.equals(authorId, userId)) {
            throw new ConflictException("Only author can change comment");
        }

        if (eventId != null && inputEventId != null && !Objects.equals(eventId, inputEventId)) {
            throw new ConflictException("Wrong author/event");
        }
    }
}
