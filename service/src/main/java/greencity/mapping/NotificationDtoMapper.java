package greencity.mapping;

import greencity.dto.notification.NotificationEvent;
import greencity.entity.Notification;
import java.util.Collections;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NotificationDtoMapper extends AbstractConverter<Notification, NotificationEvent> {
    @Override
    public NotificationEvent convert(Notification notification) {
        return NotificationEvent.builder()
            .eventType(notification.getEventType())
            .targetUserId(notification.getUserId())
            .source(notification.getSource())
            .timestamp(notification.getTimestamp())
            .payload(Collections.emptyMap()) // temporarily null
            .build();
    }
}
