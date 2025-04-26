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
        List<EventDateLocation> eventDateLocations = addEventDtoRequest.datesLocations().stream()
            .map(dto -> EventDateLocation.builder()
                .startTime(dto.startDate())
                .endTime(dto.finishDate())
                .address(Address.builder()
                    .longitude(dto.coordinates().longitude())
                    .latitude(dto.coordinates().latitude())
                    .build())
                .onlineLink(dto.onlineLink())
                .build())
            .collect(Collectors.toList());
        return Event.builder()
            .title(addEventDtoRequest.title())
            .description(addEventDtoRequest.description())
            .isOpen(addEventDtoRequest.isOpen())
            .eventDatesLocations(eventDateLocations)
            .build();
    }
}
