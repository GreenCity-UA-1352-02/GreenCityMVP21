package greencity.service;

import greencity.dto.notification.NotificationDtoRequest;
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
     * Retrieves all notifications for specific user.
     *
     * @param id ID of the specific user
     * @return List of all notifications for specific user
     */
    List<NotificationDtoRequest> findUserNotifications(Long id, String filter);

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

    void deleteNotification(Long notificationId, Long userId);
}
