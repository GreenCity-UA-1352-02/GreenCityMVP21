package greencity.dto.event;

import greencity.dto.tag.TagUaEnDto;

import java.util.List;
import lombok.Builder;

@Builder
public record EventDto(
        Long id,
        List<String> additionalImages,
        List<EventDateLocationDto> dates,
        String description,
        boolean isFavorite,
        boolean isSubscribed,
        boolean open,
        EventAuthorDto organizer,
        List<TagUaEnDto> tags,
        String title,
        String titleImage
) {
}
