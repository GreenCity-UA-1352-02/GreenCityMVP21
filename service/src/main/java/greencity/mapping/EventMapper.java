package greencity.mapping;

import greencity.dto.event.EventDto;
import greencity.entity.event.Event;
import org.modelmapper.AbstractConverter;

public class EventMapper extends AbstractConverter<EventDto, Event> {
    @Override
    protected Event convert(EventDto eventDto) {
        return Event.builder()
            .title(eventDto.getTitle())
            .description(eventDto.getDescription())
            .isOpen(eventDto.isOpen())
            .build();
    }
}
