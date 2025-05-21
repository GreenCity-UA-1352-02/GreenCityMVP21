package greencity.repository;

import greencity.entity.event.EventReaction;
import greencity.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EventReactionRepository extends JpaRepository<EventReaction, Long> {
    Optional<EventReaction> findByUserIdAndEventId(Long user, Long event);

    long countByEventIdAndReactionType(Long eventId, ReactionType reactionType);
}
