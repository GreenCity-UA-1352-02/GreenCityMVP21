package greencity.dto.notification;

import greencity.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
    @NotNull
    private NotificationType eventType;

    /**
     * ID of the user who should receive the notification.
     */
    @NotNull
    private Long targetUserId;

    /**
     * Source of the notification.
     */
    @NotNull
    private String source;

    /**
     * Additional data specific to the event type.
     */
    @NotNull
    private NotificationPayloadDto payload;

    /**
     * Timestamp when the event occurred.
     */
    @NotNull
    @PastOrPresent
    private LocalDateTime timestamp;
}
