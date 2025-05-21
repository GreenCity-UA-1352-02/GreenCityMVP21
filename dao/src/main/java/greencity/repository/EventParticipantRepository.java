package greencity.repository;

import java.util.List;
import java.util.Optional;
import greencity.entity.event.Event;
import greencity.entity.event.EventParticipant;
import greencity.enums.EventRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    List<EventParticipant> findAllByUserIdAndActiveTrue(Long userId);

    List<EventParticipant> findAllByUserIdAndRoleAndActiveTrue(Long userId, EventRole role);

    Optional<EventParticipant> findByUserIdAndEventIdAndActiveTrue(Long userId, Long eventId);

    @Query("SELECT e FROM Event e WHERE e.author.id = :userId")
    List<Event> findAllEventsByAuthorId(@Param("userId") Long userId);

    List<EventParticipant> findAllByUserId(Long userId);
}
