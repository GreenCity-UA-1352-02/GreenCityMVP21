package greencity.dto.notification;

import greencity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    /**
     * Type of event (COMMENT_CREATED, ARTICLE_LIKED, etc.).
     */
    private NotificationType eventType;

    /**
     * ID of the user who should receive the notification.
     */
    private Long targetUserId;

    /**
     * Source of the notification.
     */
    private String source;

    /**
     * Additional data specific to the event type.
     */
    private NotificationPayloadDto payload;

    /**
     * Timestamp when the event occurred.
     */
    private LocalDateTime timestamp;
}
