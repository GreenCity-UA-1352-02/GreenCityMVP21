package greencity.dto.event;

import java.time.ZonedDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventSearchDto {
    private Long id;
    private String title;
    private String description;
    private String organizer;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
}
