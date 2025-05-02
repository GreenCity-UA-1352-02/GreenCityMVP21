package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.EventDto;
import greencity.dto.user.UserVO;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.event.Address;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventImage;
import greencity.enums.EventType;
import greencity.enums.TagType;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.repository.EventRepo;
import greencity.repository.TagsRepo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    private final RestClient restClient;
    private final TagsServiceImpl tagsService;
    private final TagsRepo tagsRepo;

    @Override
    public EventDto save(AddEventDtoRequest addEventDtoRequest, List<MultipartFile> images, String email) {
        Event event = modelMapper.map(addEventDtoRequest, Event.class);

        List<EventDateLocation> dates = addEventDtoRequest.datesLocations().stream()
            .map(dateLocation -> mapDateLocationDto(dateLocation, event))
            .toList();
        event.setEventDatesLocations(dates);

        List<Tag> listTag = tagsList(addEventDtoRequest);
        event.setTags(listTag);

        UserVO user = restClient.findByEmail(email);
        User author = modelMapper.map(user, User.class);
        event.setAuthor(author);

        images.forEach(image -> {
            String link = fileService.upload(image);
            event.getImages().add(
                eventImageBuild(link, event)
            );
        });

        eventRepo.save(event);

        return modelMapper.map(event, EventDto.class);
    }

    private static EventImage eventImageBuild(String link, Event event) {
        return EventImage.builder()
            .link(link)
            .event(event)
            .build();
    }

    private List<Tag> tagsList(AddEventDtoRequest addEventDtoRequest) {
        List<String> lowerCaseTagNames = addEventDtoRequest.tags().stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
        List<Tag> tags = tagsRepo.findTagsByNamesAndType(lowerCaseTagNames, TagType.EVENT);
        if (tags.isEmpty()) {
            throw new TagNotFoundException(ErrorMessage.TAGS_NOT_FOUND);
        }
        return tags;
    }

    private EventDateLocation mapDateLocationDto(EventDateLocationDto dateLocation, Event event) {
        EventDateLocation.EventDateLocationBuilder eventDateLocationBuilder = EventDateLocation.builder()
            .event(event)
            .startTime(dateLocation.startDate())
            .endTime(dateLocation.finishDate());

        boolean isOnline = dateLocation.onlineLink() != null;
        boolean isOffline = dateLocation.coordinates() != null;

        if (isOnline && isOffline) {
            eventDateLocationBuilder
                .address(Address.builder()
                    .latitude(dateLocation.coordinates().latitude())
                    .longitude(dateLocation.coordinates().longitude())
                    .build())
                .onlineLink(dateLocation.onlineLink())
                .eventType(EventType.ONLINE_OFFLINE);
        } else if (isOffline) {
            eventDateLocationBuilder
                .address(Address.builder()
                    .latitude(dateLocation.coordinates().latitude())
                    .longitude(dateLocation.coordinates().longitude())
                    .build())
                .eventType(EventType.OFFLINE);
        } else if (isOnline) {
            eventDateLocationBuilder
                .onlineLink(dateLocation.onlineLink())
                .eventType(EventType.ONLINE);
        }

        return eventDateLocationBuilder.build();
    }
}
