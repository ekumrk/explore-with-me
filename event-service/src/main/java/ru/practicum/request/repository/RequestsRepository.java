package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestsRepository extends JpaRepository<Request, Long> {
    Optional<Request> findFirstByEventIdAndRequesterId(Long eventId, Long requesterId);

    @Query("Select COUNT(r) " +
            "FROM Request r " +
            "WHERE r.event.id = ?1 " +
            "AND r.status = 'CONFIRMED' ")
    Long findAllByEventIdAndStatusConfirmed(Long eventId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByRequesterId(Long requesterId);

    @Query("Select r " +
            "FROM Request r " +
            "WHERE r.id in (?1) ")
    List<Request> findAllByIdIn(List<Long> ids);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);
}
