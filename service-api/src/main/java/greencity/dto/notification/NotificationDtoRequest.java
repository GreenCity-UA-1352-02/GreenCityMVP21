package greencity.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDtoRequest {
    private String actorName;
    private String action;
    private String object;
    private String timestamp;
    private String status;
}
