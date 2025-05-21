package greencity.mapping;

import greencity.dto.notification.NotificationEvent;
import greencity.dto.notification.NotificationPayloadDto;
import greencity.entity.Notification;
import greencity.entity.NotificationPayload;
import greencity.enums.NotificationType;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationDtoMapperTest {
    private final NotificationDtoMapper notificationDtoMapper = new NotificationDtoMapper();

    @Test
    void convert_validNotification_returnsNotificationEvent() {
        Notification notification = Notification.builder()
            .id(1L)
            .eventType(NotificationType.COMMENT_CREATED)
            .source("source")
            .userId(4L)
            .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .payload(NotificationPayload.builder()
                .actorId(3L)
                .actorName("actor")
                .articleId(1L)
                .articleTitle("article")
                .objectType("ARTICLE")
                .build())
            .build();

        NotificationEvent expected = NotificationEvent.builder()
            .eventType(NotificationType.COMMENT_CREATED)
            .source("source")
            .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .targetUserId(4L)
            .payload(NotificationPayloadDto.builder()
                .actorId(3L)
                .actorName("actor")
                .articleId(1L)
                .articleTitle("article")
                .objectType("ARTICLE")
                .build())
            .build();

        NotificationEvent actual = notificationDtoMapper.convert(notification);

        assertEquals(expected, actual);
    }
}
