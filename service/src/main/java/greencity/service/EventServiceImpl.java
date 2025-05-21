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
import greencity.enums.TagType;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.TagNotFoundException;
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
    private final EventDateLocationService eventDateLocationService;
    private final EventImageService eventImageService;

    @Override
    @Transactional
    public EventResponse save(AddEventRequest addEventRequest, List<MultipartFile> images, String email) {
        Event event = prepareEvent(addEventRequest, email);
        eventRepo.save(event);

        assignDatesLocations(event, addEventRequest.datesLocations());
        assignMainImage(event, images.removeFirst());
        assignAdditionalImages(event, images);

        return buildResponse(event);
    }

    private void assignDatesLocations(Event event, List<EventDateLocationDto> dtos) {
        List<EventDateLocation> datesLocations = dtos.stream()
            .map(dateLocationDto -> {
                EventDateLocationDto dateLocation = eventDateLocationService.save(dateLocationDto, event.getId());
                EventDateLocation eventDateLocation = modelMapper.map(dateLocation, EventDateLocation.class);
                eventDateLocation.setEvent(event);

                eventDateLocation.setAddress(dateLocationDto.coordinates() == null ? null :
                    modelMapper.map(dateLocationDto.coordinates(), Address.class));
                return eventDateLocation;
            })
            .toList();
        event.setEventDatesLocations(datesLocations);
    }

    private Event prepareEvent(AddEventRequest request, String email) {
        Event event = modelMapper.map(request, Event.class);
        event.setTags(parseTags(request.tags()));
        event.setAuthor(getUser(email));
        return event;
    }

    private List<Tag> parseTags(List<String> rawTags) {
        List<String> lowerCaseTagNames = rawTags.stream()
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

    private User getUser(String email) {
        UserVO user = restClient.findByEmail(email);
        return modelMapper.map(user, User.class);
    }

    private void assignMainImage(Event event, MultipartFile image) {
        EventImageDto mainImage = eventImageService.uploadImage(image, event.getId());
        EventImage eventImage = mapToEntity(mainImage, event);
        event.setMainImage(eventImage);
        event.getImages().add(eventImage);
    }

    private EventImage mapToEntity(EventImageDto image, Event event) {
        return EventImage.builder()
            .id(image.id())
            .link(image.link())
            .event(event)
            .build();
    }

    private void assignAdditionalImages(Event event, List<MultipartFile> images) {
        List<EventImageDto> uploaded = eventImageService.uploadImages(images, event.getId());

        List<EventImage> eventImages = uploaded.stream()
            .map(image -> mapToEntity(image, event))
            .toList();

        event.setImages(eventImages);
    }

    private EventResponse buildResponse(Event event) {
        EventResponse dto = modelMapper.map(event, EventResponse.class);

        List<String> images = event.getImages().stream()
            .map(EventImage::getLink)
            .toList();
        dto.setAdditionalImages(images);

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
                    .id(date.getId())
                    .startDate(date.getStartTime())
                    .finishDate(date.getEndTime())
                    .coordinates(coordinates)
                    .onlineLink(date.getOnlineLink())
                    .build();
            })
            .toList();
        dto.setDates(dates);

        dto.setTags(addTagUaEnDtos(event.getTags()));
        dto.setTitleImage(event.getMainImage().getLink());
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

    @Override
    @Transactional
    public EventResponse update(UpdateEventRequest updateEventRequest, List<MultipartFile> images) {
        Event event = eventRepo.findById(updateEventRequest.id())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.WRONG_EVENT_ID));

        updateMainFields(event, updateEventRequest);
        updateTags(event, updateEventRequest.tags());
        updateDatesLocations(updateEventRequest.datesLocations());
        updateImages(event, images);

        return buildResponse(event);
    }

    private void updateMainFields(Event event, UpdateEventRequest updateEventRequest) {
        event.setTitle(updateEventRequest.title());
        event.setDescription(updateEventRequest.description());
        event.setOpen(updateEventRequest.isOpen());
    }

    private void updateTags(Event event, List<String> rawTags) {
        event.setTags(parseTags(rawTags));
    }

    private void updateDatesLocations(List<EventDateLocationDto> dtos) {
        dtos.forEach(eventDateLocationService::update);
    }

    private void updateImages(Event event, List<MultipartFile> images) {
        assignMainImage(event, images.removeFirst());
        eventImageService.deleteImagesByEventIdExceptMain(event.getId());
        assignAdditionalImages(event, images);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Event event = eventRepo.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.WRONG_EVENT_ID));

        deleteImages(event);
        deleteDates(event);
        eventRepo.delete(event);
    }

    @Override
    public EventVO findById(Long id) {
        Event event = eventRepo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND + id));
        return modelMapper.map(event, EventVO.class);
    }

    private void deleteImages(Event event) {
        fileService.delete(event.getMainImage().getLink());
        eventImageService.deleteImagesByEventId(event.getId());
    }

    private void deleteDates(Event event) {
        event.getEventDatesLocations().forEach(dateLocation ->
            eventDateLocationService.delete(dateLocation.getId()));
    }
}
