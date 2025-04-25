package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AddEventDtoRequest(
        String title,
        String description,
        @JsonProperty("open")
        boolean isOpen,
        List<EventDateLocationDto> datesLocations,
        List<String> tags
) {
}
