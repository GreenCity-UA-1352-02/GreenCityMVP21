package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Builder
public record UpdateEventRequest(
    @NotNull
    long id,
    @Size(max = 70, message = "Title must be no longer than 70 characters")
    String title,
    @Size(min = 20, max = 63206, message = "Description must be between 20 and 63,206 characters")
    String description,
    @JsonProperty("open")
    boolean isOpen,
    @NotEmpty
    List<@Valid EventDateLocationDto> datesLocations,
    @NotEmpty
    List<String> tags
) {
}
