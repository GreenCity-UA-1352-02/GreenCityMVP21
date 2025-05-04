package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import greencity.dto.notification.NotificationEvent;
import greencity.dto.notification.NotificationPayloadDto;
import greencity.entity.Notification;
import greencity.entity.NotificationPayload;
import greencity.enums.NotificationStatus;
import greencity.enums.NotificationType;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

public class NotificationEventMapperTest {

    private final NotificationEventMapper notificationMapper = new NotificationEventMapper();

    @Test
    public void convert_validNotificationEvent_returnsNotification() {
        NotificationEvent notificationEvent = NotificationEvent.builder()
            .eventType(NotificationType.COMMENT_CREATED)
            .source("GREENCITY")
            .targetUserId(2L)
            .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .payload(NotificationPayloadDto.builder()
                .actorId(1L)
                .actorName("actor")
                .articleId(1L)
                .articleTitle("article")
                .objectType("ARTICLE")
                .build())
            .build();

        Notification expected = Notification.builder()
            .eventType(NotificationType.COMMENT_CREATED)
            .source("GREENCITY")
            .userId(2L)
            .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .status(NotificationStatus.UNREAD)
            .payload(NotificationPayload.builder()
                .actorId(1L)
                .actorName("actor")
                .articleId(1L)
                .articleTitle("article")
                .objectType("ARTICLE")
                .build())
            .build();

        Notification actual = notificationMapper.convert(notificationEvent);

        assertEquals(expected, actual);

    }
}
