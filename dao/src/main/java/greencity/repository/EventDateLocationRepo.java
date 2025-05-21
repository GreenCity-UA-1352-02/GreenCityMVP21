package greencity.repository;

import greencity.entity.event.EventDateLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDateLocationRepo extends JpaRepository<EventDateLocation, Long> {
}