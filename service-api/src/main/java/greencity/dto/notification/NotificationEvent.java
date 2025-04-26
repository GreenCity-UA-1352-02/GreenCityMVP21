package greencity.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    /**
     * Type of event (COMMENT_CREATED, ARTICLE_LIKED, etc.).
     */
    private String eventType;

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
    private Map<String, Object> payload;

    /**
     * Timestamp when the event occurred.
     */
    private LocalDateTime timestamp;
}
