package greencity.mapping;

import greencity.dto.event.AddEventDtoRequest;
import greencity.entity.event.Event;
import org.modelmapper.AbstractConverter;

public class AddEventDtoRequestMapper extends AbstractConverter<AddEventDtoRequest, Event> {
    @Override
    protected Event convert(AddEventDtoRequest addEventDtoRequest) {
        return Event.builder()
            .title(addEventDtoRequest.title())
            .description(addEventDtoRequest.description())
            .isOpen(addEventDtoRequest.isOpen())
            .build();
    }
}
