package greencity.repository;

import greencity.entity.NotificationPayload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationPayloadRepo extends JpaRepository<NotificationPayload, Long> {
}
