package greencity.mapping;

import greencity.dto.event.EventImageDto;
import greencity.entity.event.EventImage;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventImageMapper extends AbstractConverter<EventImage, EventImageDto> {
    @Override
    protected EventImageDto convert(EventImage source) {
        return EventImageDto.builder()
            .id(source.getId())
            .link(source.getLink())
            .build();
    }
}
