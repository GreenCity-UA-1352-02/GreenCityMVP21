package greencity.mapping;

import greencity.dto.event.EventResponse;
import greencity.entity.event.Event;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventDtoMapper extends AbstractConverter<Event, EventResponse> {
    @Override
    protected EventResponse convert(Event event) {
        return EventResponse.builder()
            .id(event.getId())
            .description(event.getDescription())
            .open(event.isOpen())
            .title(event.getTitle())
            .titleImage(event.getMainImage().getLink())
            .build();
    }
}
