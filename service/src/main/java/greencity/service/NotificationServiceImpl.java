package greencity.service;

import greencity.dto.notification.NotificationDtoRequest;
import greencity.dto.notification.NotificationEvent;
import greencity.entity.Notification;
import greencity.mapping.NotificationDtoMapper;
import greencity.mapping.NotificationDtoRequestMapper;
import greencity.mapping.NotificationEventMapper;
import greencity.repository.NotificationRepo;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final NotificationDtoMapper notificationDtoMapper;
    private final NotificationEventMapper notificationEventMapper;
    private final NotificationDtoRequestMapper notificationDtoRequestMapper;

    /**
     * Retrieves all notifications from the repository and maps them to notification
     * events.
     *
     * @return List of all notification events
     */
    @Override
    public List<NotificationEvent> findAllNotifications() {
        return notificationRepo.findAll().stream()
            .map(notificationDtoMapper::convert)
            .toList();
    }

    /**
     * Retrieves all notifications from the repository for user and maps them to notification
     * request.
     *
     * @param id ID of the user
     * @return List of all notification events
     */
    @Override
    public List<NotificationDtoRequest> findUserNotifications(Long id) {
        return notificationRepo.findNotificationByUserId(id).stream()
            .map(notificationDtoRequestMapper::convert)
            .toList();
    }

    /**
     * Retrieves a specific notification by its ID and maps it to a notification
     * event.
     *
     * @param id ID of the notification to retrieve
     * @return Notification event corresponding to the given ID
     * @throws EntityNotFoundException if the notification with the given ID does
     *                                 not exist
     */
    @Override
    public NotificationEvent findNotificationById(Long id) {
        Notification notification = notificationRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        return notificationDtoMapper.convert(notification);
    }

    /**
     * Saves a new notification event by converting it into a notification entity
     * and persisting it.
     *
     * @param notification Notification event to save
     * @return Saved notification event after persistence
     */
    @Override
    public NotificationEvent saveNotification(NotificationEvent notification) {
        return notificationDtoMapper.convert(notificationRepo.save(notificationEventMapper.convert(notification)));
    }
}
