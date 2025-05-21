package greencity.dto.event;

import lombok.Builder;

@Builder
public record EventImageDto(
    Long id,
    String link
) {
}