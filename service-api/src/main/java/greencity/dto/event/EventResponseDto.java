package greencity.dto.event;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class EventResponseDto {
    private String title;

    private String description;

    private String visability;

    private List<EventDateDetailsDto> event;

    private ZonedDateTime createdAt;

    private Long authorId;

    private List<String> tag;

    private List<EventImageDto> images;

    private String mainImage;

    private List<Long> participantIds;
}
