package greencity.dto.event;

import greencity.annotations.EventTimeValidation;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
@EventTimeValidation
public record EventDateLocationDto(
        ZonedDateTime startDate,
        ZonedDateTime finishDate,
        AddressDto coordinates,
        String onlineLink
) {
}
