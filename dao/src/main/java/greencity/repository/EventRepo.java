package greencity.repository;

import greencity.entity.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepo extends JpaRepository<Event, Long> {
    boolean existsByIdAndAuthor_Email(Long id, String email);
}