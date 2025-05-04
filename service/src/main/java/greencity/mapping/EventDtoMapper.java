package greencity.mapping;

import greencity.dto.event.EventDto;
import greencity.entity.event.Event;
import org.modelmapper.AbstractConverter;

public class EventDtoMapper extends AbstractConverter<Event, EventDto> {
    @Override
    protected EventDto convert(Event event) {
        return EventDto.builder()
            .id(event.getId())
            .description(event.getDescription())
            .open(event.isOpen())
            .title(event.getTitle())
            .titleImage(event.getMainImage().getLink())
            .build();
    }
}
