package greencity.mapping;

import greencity.dto.event.EventResponse;
import greencity.entity.event.Event;
import org.modelmapper.AbstractConverter;

public class EventMapper extends AbstractConverter<EventResponse, Event> {
    @Override
    protected Event convert(EventResponse eventResponse) {
        return Event.builder()
            .title(eventResponse.getTitle())
            .description(eventResponse.getDescription())
            .isOpen(eventResponse.isOpen())
            .build();
    }
}
