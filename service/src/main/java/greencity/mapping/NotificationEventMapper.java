package greencity.mapping;

import greencity.dto.notification.NotificationEvent;
import greencity.entity.Notification;
import greencity.entity.NotificationPayload;
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
        notification.setPayload(NotificationPayload.builder()
            .actorId(notificationEvent.getPayload().getActorId())
            .actorName(notificationEvent.getPayload().getActorName())
            .articleId(notificationEvent.getPayload().getArticleId())
            .articleTitle(notificationEvent.getPayload().getArticleTitle())
            .objectType(notificationEvent.getPayload().getObjectType())
            .build());
        return notification;
    }
}
