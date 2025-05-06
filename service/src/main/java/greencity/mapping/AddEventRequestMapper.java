package greencity.mapping;

import greencity.dto.event.AddEventRequest;
import greencity.entity.event.Event;
import org.modelmapper.AbstractConverter;

public class AddEventRequestMapper extends AbstractConverter<AddEventRequest, Event> {
    @Override
    protected Event convert(AddEventRequest addEventRequest) {
        return Event.builder()
            .title(addEventRequest.title())
            .description(addEventRequest.description())
            .isOpen(addEventRequest.isOpen())
            .build();
    }
}
