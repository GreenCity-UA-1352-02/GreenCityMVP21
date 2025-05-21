package greencity.repository;

import greencity.entity.NotificationPayload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationPayloadRepo extends JpaRepository<NotificationPayload, Long> {
    Optional<NotificationPayload> findByArticleIdAndObjectType(Long articleId, String objectType);

}
