package greencity.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationPayloadDto {
    private Long actorId;

    private String actorName;

    private Long articleId;

    private String articleTitle;

    private String objectType;
}
