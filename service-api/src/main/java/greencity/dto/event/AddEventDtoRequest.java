package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;

@Builder
public record AddEventDtoRequest(
    String title,
    String description,
    @JsonProperty("open")
    boolean isOpen,
    List<EventDateLocationDto> datesLocations,
    List<String> tags
) {
}
