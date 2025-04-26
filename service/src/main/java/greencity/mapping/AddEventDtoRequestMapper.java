package greencity.mapping;

import greencity.dto.event.AddEventDtoRequest;
import greencity.entity.event.Address;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import java.util.List;
import java.util.stream.Collectors;
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
