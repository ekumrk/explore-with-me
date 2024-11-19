package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(long initiatorId, Pageable page);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE ( (e.initiator.id IN (?1))) " +
            "AND ( (e.state IN (?2)) ) " +
            "AND ( (e.category.id IN (?3)) ) " +
            "AND (e.eventDate BETWEEN ?4 AND ?5) ")
    List<Event> findAllByAdmin(List<Long> users, List<EventState> states, List<Integer> categories,
                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.state='PUBLISHED' " +
            "AND (" +
            "LOWER(e.annotation) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', ?1, '%'))" +
            ") " +
            "AND ( (e.category.id IN (?2)) ) " +
            "AND ( (e.paid = ?3) ) " +
            "AND (e.eventDate BETWEEN ?4 AND ?5)")
    List<Event> findAllByPublic(String text, List<Integer> categories, Boolean paid,
                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);

    List<Event> findAllByCategoryId(int categoryId);
}
