package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/users/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto add(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId,
                          @RequestBody @Valid NewCommentDto dto) {
        return service.addNewComment(userId, eventId, dto);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/comments/{commentId}")
    public ShortCommentDto update(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId,
                                  @PathVariable @Positive Long commentId, @RequestBody @Valid NewCommentDto dto) {
        return service.update(userId, eventId, commentId, dto);
    }

    @PatchMapping("admin/events/{eventId}/comments/{commentId}")
    public CommentDto adminUpdate(@PathVariable @Positive Long eventId,
                                  @PathVariable @Positive Long commentId, @RequestBody @Valid CommentDto dto) {
        return service.adminUpdate(eventId, commentId, dto);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long userId, @PathVariable @Positive Long commentId) {
        service.delete(userId, commentId);
    }

    @DeleteMapping("admin/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminDelete(@PathVariable @Positive Long userId, @PathVariable @Positive Long commentId) {
        service.adminDelete(userId, commentId);
    }

    @GetMapping("events/{eventId}/comments")
    public List<ShortCommentDto> getComments(@PathVariable @Positive Long eventId) {
        return service.getEventComments(eventId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/comments")
    public List<ShortCommentDto> getUserComments(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId) {
        return service.getEventUserComments(userId, eventId);
    }

    @GetMapping("admin/events/{eventId}/comments")
    public List<CommentDto> getAdminComments(@PathVariable @Positive Long eventId) {
        return service.getAdminEventComments(eventId);
    }

}
