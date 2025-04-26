package greencity.mapping;

import greencity.dto.notification.NotificationEvent;
import greencity.entity.Notification;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventMapper extends AbstractConverter<NotificationEvent, Notification> {
    @Override
    public Notification convert(NotificationEvent notificationEvent) {
        Notification notification = new Notification();
        notification.setEventType(notificationEvent.getEventType());
        notification.setSource(notificationEvent.getSource());
        notification.setUserId(notificationEvent.getTargetUserId());
        notification.setTimestamp(notificationEvent.getTimestamp());
        return notification;
    }
}
