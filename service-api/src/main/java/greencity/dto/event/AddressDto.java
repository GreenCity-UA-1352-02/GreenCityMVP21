package greencity.dto.event;

import lombok.Builder;

@Builder
public record AddressDto(
        Double latitude,
        Double longitude
) {
}
