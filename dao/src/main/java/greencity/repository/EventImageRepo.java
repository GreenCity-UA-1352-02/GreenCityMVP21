package greencity.repository;

import greencity.entity.event.EventImage;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventImageRepo extends JpaRepository<EventImage, Long> {
    EventImage findByLink(@NotBlank String link);
}