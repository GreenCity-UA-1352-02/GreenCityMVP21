package greencity.service;

import greencity.dto.notification.NotificationDtoRequest;
import greencity.dto.notification.NotificationEvent;
import greencity.entity.Notification;
import greencity.enums.NotificationStatus;
import greencity.exception.exceptions.NotificationNotFound;
import greencity.mapping.NotificationDtoMapper;
import greencity.mapping.NotificationDtoRequestMapper;
import greencity.mapping.NotificationEventMapper;
import greencity.repository.NotificationRepo;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
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
     * Retrieves all notifications for a user based on a filter, updates their status if unread,
     * and schedules them for deletion or removes them if expired.
     *
     * <p>If the filter is "ALL", all user notifications are returned. Otherwise, only notifications
     * with a matching source are included.</p>
     *
     * <p>Unread notifications are marked as READ and scheduled for deletion after 5 minutes from the current time.
     * If a notification is already READ and its deletion timestamp has passed, it is permanently removed.</p>
     *
     * @param id     ID of the user
     * @param filter Filter for notification source (e.g., "EMAIL", "SYSTEM", or "ALL")
     * @return List of filtered and mapped notification DTOs
     */
    @Override
    public List<NotificationDtoRequest> findUserNotifications(Long id, String filter) {
        List<NotificationDtoRequest> notifications;

        if ("ALL".equalsIgnoreCase(filter)) {
            notifications = notificationRepo.findNotificationByUserId(id).stream()
                .map(notificationDtoRequestMapper::convert)
                .toList();
        } else {
            notifications = notificationRepo.findNotificationByUserId(id).stream()
                .filter(notification -> notification.getSource().equals(filter.toUpperCase()))
                .map(notificationDtoRequestMapper::convert)
                .toList();
        }
        for (Notification notification : notificationRepo.findNotificationByUserId(id)) {
            if (notification.getStatus() == NotificationStatus.UNREAD) {
                notification.setStatus(NotificationStatus.READ);
                notification.setTimestampDeletion(LocalDateTime.now().plusMinutes(5));
                notificationRepo.save(notification);
            } else {
                if (LocalDateTime.now().isAfter(notification.getTimestampDeletion())) {
                    notificationRepo.delete(notification);
                }
            }
        }
        return notifications;
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

    @Override
    public void deleteNotification(Long notificationId, Long userId) {
        List<Notification> notifications = notificationRepo.findNotificationByUserId(userId);

        boolean found = false;
        for (Notification notification : notifications) {
            if (notification.getId().equals(notificationId)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new NotificationNotFound("Notification not found for user");
        }
        notificationRepo.deleteById(notificationId);
    }
}
