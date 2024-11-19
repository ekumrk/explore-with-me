package ru.practicum.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventId(Long eventId);

    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.event.id in (?1)")
    List<Comment> findAllByEventIdIn(List<Long> eventIds);

    List<Comment> findAllByEventIdAndAuthorId(Long eventId, Long authorId);
}
