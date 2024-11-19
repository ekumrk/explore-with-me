package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.model.dto.CommentDto;
import ru.practicum.comment.model.dto.NewCommentDto;
import ru.practicum.comment.model.dto.ShortCommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService service;

    @RequestMapping(value = "/users/{userId}/events/{eventId}/comments", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto add(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId,
                          @RequestBody @Valid NewCommentDto dto) {
        return service.addNewComment(userId, eventId, dto);
    }

    @RequestMapping(value = "/users/{userId}/events/{eventId}/comments/{commentId}", method = RequestMethod.PATCH)
    public ShortCommentDto update(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId,
                                  @PathVariable @Positive Long commentId, @RequestBody @Valid NewCommentDto dto) {
        return service.update(userId, eventId, commentId, dto);
    }

    @RequestMapping(value = "admin/events/{eventId}/comments/{commentId}", method = RequestMethod.PATCH)
    public CommentDto adminUpdate(@PathVariable @Positive Long eventId,
                                  @PathVariable @Positive Long commentId, @RequestBody @Valid CommentDto dto) {
        return service.adminUpdate(eventId, commentId, dto);
    }

    @RequestMapping(value = "/users/{userId}/comments/{commentId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long userId, @PathVariable @Positive Long commentId) {
        service.delete(userId, commentId);
    }

    @RequestMapping(value = "admin/users/{userId}/comments/{commentId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminDelete(@PathVariable @Positive Long userId, @PathVariable @Positive Long commentId) {
        service.adminDelete(userId, commentId);
    }

    @RequestMapping(value = "events/{eventId}/comments", method = RequestMethod.GET)
    public List<ShortCommentDto> getComments(@PathVariable @Positive Long eventId) {
        return service.getEventComments(eventId);
    }

    @RequestMapping(value = "/users/{userId}/events/{eventId}/comments", method = RequestMethod.GET)
    public List<ShortCommentDto> getUserComments(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId) {
        return service.getEventUserComments(userId, eventId);
    }

    @RequestMapping(value = "admin/events/{eventId}/comments", method = RequestMethod.GET)
    public List<CommentDto> getAdminComments(@PathVariable @Positive Long eventId) {
        return service.getAdminEventComments(eventId);
    }

}
