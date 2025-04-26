package greencity.dto.event;

import lombok.Builder;

@Builder
public record EventAuthorDto(
        Long id,
        String name,
        Double organizerRating
) {
}
