package ru.practicum.comment.service;

import ru.practicum.comment.model.dto.CommentDto;
import ru.practicum.comment.model.dto.NewCommentDto;
import ru.practicum.comment.model.dto.ShortCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addNewComment(Long userId, Long eventId, NewCommentDto dto);

    ShortCommentDto update(Long userId, Long eventId, Long commentId, NewCommentDto dto);

    CommentDto adminUpdate(Long eventId, Long commentId, CommentDto dto);

    void delete(Long userId, Long commentId);

    void adminDelete(Long userId, Long commentId);

    List<ShortCommentDto> getEventComments(Long eventId);

    List<ShortCommentDto> getEventUserComments(Long userId, Long eventId);

    List<CommentDto> getAdminEventComments(Long eventId);
}
