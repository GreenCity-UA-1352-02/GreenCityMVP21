package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Builder
public record AddEventRequest(
    @Size(max = 70, message = "Title must be no longer than 70 characters")
    String title,
    @Size(min = 20, max = 63206, message = "Description must be between 20 and 63,206 characters")
    String description,
    @JsonProperty("open")
    boolean isOpen,
    List<@Valid EventDateLocationDto> datesLocations,
    List<String> tags
) {
}
