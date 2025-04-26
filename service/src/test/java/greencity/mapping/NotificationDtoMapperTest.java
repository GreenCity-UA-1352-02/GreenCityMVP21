package greencity.mapping;

import greencity.dto.notification.NotificationEvent;
import greencity.entity.Notification;
import greencity.enums.NotificationType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationDtoMapperTest {
    private final NotificationDtoMapper notificationDtoMapper = new NotificationDtoMapper();

    @Test
    void convert_validNotification_returnsNotificationEvent() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setEventType(NotificationType.COMMENT_CREATED);
        notification.setSource("source");
        notification.setUserId(4L);
        notification.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        NotificationEvent expected = NotificationEvent.builder()
            .eventType(NotificationType.COMMENT_CREATED)
            .source("source")
            .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .targetUserId(4L)
            .payload(Collections.emptyMap())
            .build();

        NotificationEvent actual = notificationDtoMapper.convert(notification);

        assertEquals(expected, actual);
    }
}
