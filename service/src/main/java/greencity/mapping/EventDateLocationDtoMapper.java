package greencity.mapping;

import greencity.dto.event.EventDateLocationDto;
import greencity.entity.event.EventDateLocation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventDateLocationDtoMapper extends AbstractConverter<EventDateLocationDto, EventDateLocation> {
    @Override
    protected EventDateLocation convert(EventDateLocationDto source) {
        return EventDateLocation.builder()
            .id(source.id())
            .startTime(source.startDate())
            .endTime(source.finishDate())
            .onlineLink(source.onlineLink())
            .build();
    }
}
