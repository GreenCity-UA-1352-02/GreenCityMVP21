package greencity.dto.event;

import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record EventDateLocationDto(
        ZonedDateTime startDate,
        ZonedDateTime finishDate,
        AddressDto coordinates,
        String onlineLink
) {
}
