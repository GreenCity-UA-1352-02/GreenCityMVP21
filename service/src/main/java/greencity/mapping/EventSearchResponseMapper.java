package greencity.mapping;

import greencity.dto.event.EventSearchDto;
import greencity.entity.event.Event;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventSearchResponseMapper extends AbstractConverter<Event, EventSearchDto> {
    @Override
    public EventSearchDto convert(Event event) {
        return EventSearchDto.builder()
            .id(event.getId())
            .title(event.getTitle())
            .description(event.getDescription())
            .organizer(event.getAuthor().getName())
            .startDate(event.getEventDatesLocations().getFirst().getStartTime())
            .endDate(event.getEventDatesLocations().getFirst().getEndTime())
            .build();
    }
}
