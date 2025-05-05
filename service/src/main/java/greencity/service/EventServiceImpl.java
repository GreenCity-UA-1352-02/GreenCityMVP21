package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.event.*;
import greencity.dto.tag.TagUaEnDto;
import greencity.dto.user.UserVO;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.event.Address;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventImage;
import greencity.entity.localization.TagTranslation;
import greencity.enums.EventType;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepo;
import greencity.repository.TagsRepo;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    private final RestClient restClient;
    private final TagsRepo tagsRepo;

    @Override
    @Transactional
    public EventDto save(AddEventDtoRequest addEventDtoRequest, List<MultipartFile> images, String email) {
        Event event = modelMapper.map(addEventDtoRequest, Event.class);

        List<EventDateLocation> dates = addEventDtoRequest.datesLocations().stream()
            .map(dateLocation -> mapDateLocationDto(dateLocation, event))
            .toList();
        event.setEventDatesLocations(dates);

        List<Tag> listTag = tags(addEventDtoRequest);
        event.setTags(listTag);

        User author = getUser(email);
        event.setAuthor(author);

        addEventImages(images, event);

        eventRepo.save(event);

        return buildResponse(event);
    }

    @Override
    @Transactional
    public EventDto update(EventDto eventDto, List<MultipartFile> images, String name) {
        Event event =
            eventRepo.findById(eventDto.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.WRONG_EVENT_ID));
        User user = getUser(name);

        if (!event.getAuthor().getEmail().equals(name) || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setOpen(eventDto.isOpen());
        event.setTags(updateTags(eventDto));


        eventRepo.save(event);
        return buildResponse(event);
    }

    private List<Tag> updateTags(EventDto eventDto) {
        List<Tag> tags = tagsRepo.findTagsByNamesAndType(
            eventDto.getTags().stream()
                .map(TagUaEnDto::getNameEn)
                .map(String::toLowerCase)
                .toList(),
            TagType.EVENT
        );
        if (tags.isEmpty()) {
            throw new TagNotFoundException(ErrorMessage.TAG_NOT_FOUND);
        }
        return tags;
    }

    @Override
    @Transactional
    public void delete(Long id, String name) {
        Event event =
            eventRepo.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.WRONG_EVENT_ID));

        User user = getUser(name);

        if (!event.getAuthor().getEmail().equals(name) || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        if (event.getMainImage() != null) {
            fileService.delete(event.getMainImage().getLink());
        }
        if (event.getImages() != null && !event.getImages().isEmpty()) {
            event.getImages()
                .forEach(image -> fileService.delete(image.getLink()));
        }
        eventRepo.delete(event);
    }

    private EventDto buildResponse(Event event) {
        List<String> additionalImages = event.getImages().stream()
            .map(EventImage::getLink)
            .toList();

        List<EventDateLocationDto> dates = event.getEventDatesLocations().stream()
            .map(date -> {
                AddressDto coordinates = null;
                if (date.getAddress() != null) {
                    coordinates = AddressDto.builder()
                        .latitude(date.getAddress().getLatitude())
                        .longitude(date.getAddress().getLongitude())
                        .build();
                }

                return EventDateLocationDto.builder()
                    .startDate(date.getStartTime())
                    .finishDate(date.getEndTime())
                    .coordinates(coordinates)
                    .onlineLink(date.getOnlineLink())
                    .build();
            })
            .toList();

        EventDto dto = modelMapper.map(event, EventDto.class);
        dto.setAdditionalImages(additionalImages);
        dto.setDates(dates);
        dto.setTags(addTagUaEnDtos(event.getTags()));
        dto.setOrganizer(EventAuthorDto.builder()
            .id(event.getAuthor().getId())
            .name(event.getAuthor().getName())
            .organizerRating(event.getAuthor().getRating())
            .build());
        return dto;
    }

    private List<TagUaEnDto> addTagUaEnDtos(List<Tag> tags) {
        List<TagUaEnDto> tagUaEnDtos = new ArrayList<>();
        for (Tag tag : tags) {
            String nameUa = null;
            String nameEn = null;

            for (TagTranslation translation : tag.getTagTranslations()) {
                String langCode = translation.getLanguage().getCode();
                if ("ua".equals(langCode)) {
                    nameUa = translation.getName();
                } else if ("en".equals(langCode)) {
                    nameEn = translation.getName();
                }
            }

            tagUaEnDtos.add(
                TagUaEnDto.builder()
                    .id(tag.getId())
                    .nameUa(nameUa)
                    .nameEn(nameEn)
                    .build()
            );
        }
        return tagUaEnDtos;
    }

    private void addEventImages(List<MultipartFile> images, Event event) {
        if (images == null || images.isEmpty()) {
            return;
        }
        images.forEach(image -> {
            String link = fileService.upload(image);
            event.getImages().add(
                eventImageBuild(link, event)
            );
        });
    }

    private static EventImage eventImageBuild(String link, Event event) {
        return EventImage.builder()
            .link(link)
            .event(event)
            .build();
    }

    private List<Tag> tags(AddEventDtoRequest addEventDtoRequest) {
        List<String> lowerCaseTagNames = addEventDtoRequest.tags().stream()
            .map(String::toLowerCase)
            .toList();
        List<Tag> tags = tagsRepo.findAllByTagTranslations(lowerCaseTagNames, TagType.EVENT);
        if (tags.size() != lowerCaseTagNames.size()) {
            throw new TagNotFoundException(ErrorMessage.SOME_TAGS_NOT_FOUND);
        }
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

    private User getUser(String email) {
        UserVO user = restClient.findByEmail(email);
        return modelMapper.map(user, User.class);
    }
}
