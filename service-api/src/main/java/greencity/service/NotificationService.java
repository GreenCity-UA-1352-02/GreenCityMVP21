package greencity.service;

import greencity.dto.notification.NotificationEvent;
import java.util.List;

/**
 * Service interface for managing notifications.
 */
public interface NotificationService {
    /**
     * Retrieves all notifications as notification events.
     *
     * @return List of all notification events
     */
    List<NotificationEvent> findAllNotifications();

    /**
     * Retrieves a specific notification event by its ID.
     *
     * @param id ID of the notification to retrieve
     * @return Notification event corresponding to the given ID
     */
    NotificationEvent findNotificationById(Long id);

    /**
     * Saves a notification event.
     *
     * @param notification Notification event to save
     * @return Saved notification event
     */
    NotificationEvent saveNotification(NotificationEvent notification);
}
