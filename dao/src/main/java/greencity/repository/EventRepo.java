package greencity.repository;

import greencity.entity.event.Event;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
    @Query("SELECT e.id FROM Event e")
    List<Long> getAllIds();

    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findById(@Param("id") Long id);
}