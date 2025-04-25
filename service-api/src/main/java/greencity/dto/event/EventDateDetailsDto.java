package greencity.dto.event;

import greencity.annotations.EventTimeValidation;
import greencity.annotations.EventTypeValidation;
import java.time.ZonedDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@EventTimeValidation
@EventTypeValidation
public class EventDateDetailsDto {
    private ZonedDateTime startTime;

    private ZonedDateTime endTime;

    private boolean allDay;

    private boolean isOnline;

    private boolean isPlace;

    private String offlinePlace;

    private String onlinePlace;
}
