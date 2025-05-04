package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import greencity.dto.notification.NotificationDtoRequest;
import greencity.entity.Notification;
import greencity.entity.NotificationPayload;
import greencity.enums.NotificationStatus;
import greencity.enums.NotificationType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

public class NotificationDtoRequestMapperTest {

    private final NotificationDtoRequestMapper
        notificationDtoRequestMapper = new NotificationDtoRequestMapper();

    @Test
    public void convert_validNotification_returnsNotificationDtoRequest() {
        Notification notification = Notification.builder()
            .id(1L)
            .eventType(NotificationType.COMMENT_CREATED)
            .source("source")
            .userId(4L)
            .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .status(NotificationStatus.UNREAD)
            .payload(NotificationPayload.builder()
                .actorId(3L)
                .actorName("actor")
                .articleId(1L)
                .articleTitle("article")
                .objectType("ARTICLE")
                .build())
            .build();

        NotificationDtoRequest expected = NotificationDtoRequest.builder()
            .actorName("actor")
            .action(NotificationType.COMMENT_CREATED.toString())
            .object("article")
            .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .status(NotificationStatus.UNREAD.toString())
            .build();

        NotificationDtoRequest actual = notificationDtoRequestMapper.convert(notification);

        assertEquals(expected, actual);
    }
}
