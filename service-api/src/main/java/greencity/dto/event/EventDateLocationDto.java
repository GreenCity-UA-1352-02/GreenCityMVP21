package greencity.dto.event;

import java.time.ZonedDateTime;

public record EventDateLocationDto(
        ZonedDateTime startDate,
        ZonedDateTime finishDate,
        AddressDto coordinates,
        String onlineLink
) {
}
