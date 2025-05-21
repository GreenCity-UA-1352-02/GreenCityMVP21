package greencity.repository;

import greencity.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.timestamp ASC")
    List<Notification> findNotificationByUserId(@Param("userId") Long id);

    void deleteByPayload_Id(Long payloadId);
}
