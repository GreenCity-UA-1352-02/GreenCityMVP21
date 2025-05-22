package greencity.mapping;

import greencity.dto.notification.NotificationEvent;
import greencity.dto.notification.NotificationPayloadDto;
import greencity.entity.Notification;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class NotificationDtoMapper extends AbstractConverter<Notification, NotificationEvent> {
    @Override
    public NotificationEvent convert(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationPayloadDto payloadDto = null;
        if (notification.getPayload() != null) {
            payloadDto = NotificationPayloadDto.builder()
                    .actorId(notification.getPayload().getActorId())
                    .actorName(notification.getPayload().getActorName())
                    .articleId(notification.getPayload().getArticleId())
                    .articleTitle(notification.getPayload().getArticleTitle())
                    .objectType(notification.getPayload().getObjectType())
                    .build();
        }

        return NotificationEvent.builder()
                .eventType(notification.getEventType())
                .targetUserId(notification.getUserId())
                .source(notification.getSource())
                .timestamp(notification.getTimestamp())
                .payload(NotificationPayloadDto.builder()
                        .actorId(notification.getPayload().getActorId())
                        .actorName(notification.getPayload().getActorName())
                        .articleId(notification.getPayload().getArticleId())
                        .articleTitle(notification.getPayload().getArticleTitle())
                        .objectType(notification.getPayload().getObjectType())
                        .build())
                .payload(payloadDto)
                .build();
    }
}
