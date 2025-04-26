package greencity.mapping;

import greencity.dto.event.AddressDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.EventDto;
import greencity.entity.event.Event;
import greencity.entity.event.EventImage;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;

public class EventDtoMapper extends AbstractConverter<Event, EventDto> {
    @Override
    protected EventDto convert(Event event) {
        List<String> additionalImages = event.getImages().stream()
            .map(EventImage::getLink)
            .collect(Collectors.toList());
        List<EventDateLocationDto> dates = event.getEventDatesLocations().stream()
            .map(date -> EventDateLocationDto.builder()
                .startDate(date.getStartTime())
                .finishDate(date.getEndTime())
                .coordinates(AddressDto.builder()
                    .latitude(date.getAddress().getLatitude())
                    .longitude(date.getAddress().getLongitude())
                    .build())
                .onlineLink(date.getOnlineLink())
                .build())
            .collect(Collectors.toList());
        return EventDto.builder()
            .id(event.getId())
            .additionalImages(additionalImages)
            .dates(dates)
            .description(event.getDescription())
            .open(event.isOpen())
            .organizer(EventAuthorDto.builder()
                .id(event.getAuthor().getId())
                .name(event.getAuthor().getName())
                .organizerRating(event.getAuthor().getRating())
                .build())
            .title(event.getTitle())
            .titleImage(event.getMainImage().getLink())
            .build();
    }
}
