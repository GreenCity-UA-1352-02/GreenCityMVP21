package greencity.dto.event;

import java.util.List;

public record UpdateEventRequest(
    long id,
    String title,
    String description,
    boolean isOpen,
    List<EventDateLocationDto> datesLocations,
    List<String> tags
) {
}
