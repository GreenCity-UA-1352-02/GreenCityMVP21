package greencity.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.event.*;
import greencity.dto.user.UserVO;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.event.Address;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.enums.TagType;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.mapping.EventSearchResponseMapper;
import greencity.repository.EventRepo;
import greencity.repository.TagsRepo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepo eventRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private FileService fileService;
    @Mock
    private RestClient restClient;
    @Mock
    private TagsRepo tagsRepo;
    @Mock
    private EventDateLocationService eventDateLocationService;
    @Mock
    private EventImageService eventImageService;
    @Mock
    private EventSearchResponseMapper eventSearchResponseMapper;

    @InjectMocks
    private EventServiceImpl eventService;


    private User user;
    private UserVO userVO;
    private Event event;


    @BeforeEach
    void setUp() {
        user = ModelUtils.getUser();
        userVO = ModelUtils.getUserVO();
        event = ModelUtils.getEvent();
    }

    @Test
    void save_success() {
        AddEventRequest request = ModelUtils.getAddEventRequest();
        MultipartFile image = mock(MultipartFile.class);
        List<MultipartFile> images = new ArrayList<>(List.of(image));
        EventImageDto mainImageDto = ModelUtils.getEventImageDto();
        List<EventImageDto> imageDtos = ModelUtils.getEventImageDtos();
        EventResponse response = ModelUtils.getEventResponse();

        List<Tag> tags = ModelUtils.getEventTags();

        when(modelMapper.map(request, Event.class)).thenReturn(event);
        when(tagsRepo.findAllByTagTranslations(any(), eq(TagType.EVENT))).thenReturn(tags);
        when(restClient.findByEmail(any())).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(eventDateLocationService.save(any(), eq(event.getId())))
            .thenReturn(request.datesLocations().get(0));
        when(modelMapper.map(request.datesLocations().get(0), EventDateLocation.class))
            .thenReturn(event.getEventDatesLocations().get(0));
        when(modelMapper.map(request.datesLocations().get(0).coordinates(), Address.class))
            .thenReturn(event.getEventDatesLocations().get(0).getAddress());
        when(eventImageService.uploadImage(image, event.getId()))
            .thenReturn(mainImageDto);
        when(eventImageService.uploadImages(images, event.getId())).thenReturn(imageDtos);
        when(modelMapper.map(event, EventResponse.class)).thenReturn(response);

        EventResponse result = eventService.save(request, images, user.getEmail());

        verify(eventRepo).save(event);
        assertNotNull(result);
    }

    @Test
    void save_WithoutAddress_success() {
        AddEventRequest request = ModelUtils.getAddEventRequestWithoutAddress();
        MultipartFile image = mock(MultipartFile.class);
        List<MultipartFile> images = new ArrayList<>(List.of(image));
        EventImageDto mainImageDto = ModelUtils.getEventImageDto();
        List<EventImageDto> imageDtos = ModelUtils.getEventImageDtos();
        EventResponse response = ModelUtils.getEventResponse();

        List<Tag> tags = ModelUtils.getEventTags();

        when(modelMapper.map(request, Event.class)).thenReturn(event);
        when(tagsRepo.findAllByTagTranslations(any(), eq(TagType.EVENT))).thenReturn(tags);
        when(restClient.findByEmail(any())).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(eventDateLocationService.save(any(), eq(event.getId())))
            .thenReturn(request.datesLocations().get(0));
        when(modelMapper.map(request.datesLocations().get(0), EventDateLocation.class))
            .thenReturn(event.getEventDatesLocations().get(0));
        when(eventImageService.uploadImage(image, event.getId()))
            .thenReturn(mainImageDto);
        when(eventImageService.uploadImages(images, event.getId())).thenReturn(imageDtos);
        when(modelMapper.map(event, EventResponse.class)).thenReturn(response);

        EventResponse result = eventService.save(request, images, user.getEmail());

        verify(eventRepo).save(event);
        assertNotNull(result);
    }

    @Test
    void save_tagNotFound_throwsException() {
        AddEventRequest request = ModelUtils.getAddEventRequest();

        when(modelMapper.map(request, Event.class)).thenReturn(event);
        when(tagsRepo.findAllByTagTranslations(any(), eq(TagType.EVENT)))
            .thenReturn(Collections.emptyList());

        assertThrows(TagNotFoundException.class,
            () -> eventService.save(request, new ArrayList<>(), "email@test.com"));
    }

    @Test
    void save_noTagsInRequest_throwsTagsNotFoundException() {
        AddEventRequest request = ModelUtils.getAddEventRequestWithoutTags();
        List<MultipartFile> images = new ArrayList<>();

        when(modelMapper.map(request, Event.class)).thenReturn(event);
        when(tagsRepo.findAllByTagTranslations(any(), eq(TagType.EVENT))).thenReturn(Collections.emptyList());

        TagNotFoundException exception = assertThrows(
            TagNotFoundException.class,
            () -> eventService.save(request, images, user.getEmail())
        );

        assertEquals(ErrorMessage.TAGS_NOT_FOUND, exception.getMessage());
    }

    @Test
    void update_success() {
        UpdateEventRequest request = ModelUtils.getUpdateEventRequest();
        List<MultipartFile> images = new ArrayList<>(List.of(mock(MultipartFile.class)));

        List<Tag> tags = ModelUtils.getEventTags();
        EventImageDto mainImageDto = ModelUtils.getEventImageDto();
        List<EventImageDto> additionalImageDtos = ModelUtils.getEventImageDtos();
        EventResponse response = ModelUtils.getEventResponse();

        when(eventRepo.findById(request.id())).thenReturn(Optional.of(event));
        when(tagsRepo.findAllByTagTranslations(any(), eq(TagType.EVENT))).thenReturn(tags);
        when(eventImageService.uploadImage(any(), eq(event.getId()))).thenReturn(mainImageDto);
        when(eventImageService.uploadImages(any(), eq(event.getId()))).thenReturn(additionalImageDtos);
        when(modelMapper.map(event, EventResponse.class)).thenReturn(response);

        EventResponse result = eventService.update(request, images);

        verify(eventRepo).findById(request.id());
        verify(eventImageService).deleteImagesByEventIdExceptMain(event.getId());
        assertNotNull(result);
    }

    @Test
    void update_eventNotFound_throwsException() {
        UpdateEventRequest request = ModelUtils.getUpdateEventRequest();

        when(eventRepo.findById(request.id())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.update(request, new ArrayList<>()));
    }

    @Test
    void delete_success() {
        when(eventRepo.findById(event.getId())).thenReturn(Optional.of(event));

        eventService.delete(event.getId());

        verify(fileService).delete(event.getMainImage().getLink());
        verify(eventImageService).deleteImagesByEventId(event.getId());
        verify(eventRepo).delete(event);
    }

    @Test
    void searchByTitle_whenQueryIsAll_returnsAllMappedEvents() {
        Event event1 = Event.builder()
            .title("Title 1")
            .build();
        Event event2 = Event.builder()
            .title("Title 2")
            .build();

        List<Event> events = List.of(event1, event2);

        EventSearchDto dto1 = EventSearchDto.builder()
            .title("Title 1")
            .build();
        EventSearchDto dto2 = EventSearchDto.builder()
            .title("Title 2")
            .build();

        when(eventRepo.findAll()).thenReturn(events);
        when(eventSearchResponseMapper.convert(event1)).thenReturn(dto1);
        when(eventSearchResponseMapper.convert(event2)).thenReturn(dto2);

        List<EventSearchDto> result = eventService.searchByTitle("all");

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    void searchByTitle_whenQueryMatchesSomeTitles_returnsFilteredMappedEvents() {
        Event event1 = Event.builder()
            .title("Title Hello")
            .build();
        Event event2 = Event.builder()
            .title("Title World")
            .build();

        List<Event> events = List.of(event1, event2);

        EventSearchDto dto1 = EventSearchDto.builder()
            .title("Title Hello")
            .build();
        EventSearchDto dto2 = EventSearchDto.builder()
            .title("Title World")
            .build();

        when(eventRepo.findAll()).thenReturn(events);
        when(eventSearchResponseMapper.convert(event1)).thenReturn(dto1);
        when(eventSearchResponseMapper.convert(event2)).thenReturn(dto2);

        List<EventSearchDto> result = eventService.searchByTitle("hello");

        assertEquals(1, result.size());
        assertEquals("Title Hello", result.get(0).getTitle());
        assertFalse(result.contains(dto2));
    }

    @Test
    void searchByTitle_whenQueryMatchesNothing_returnsEmptyList() {
        Event event1 = Event.builder()
            .title("Title Hello")
            .build();
        Event event2 = Event.builder()
            .title("Title World")
            .build();

        List<Event> events = List.of(event1, event2);

        EventSearchDto dto1 = EventSearchDto.builder()
            .title("Title Hello")
            .build();
        EventSearchDto dto2 = EventSearchDto.builder()
            .title("Title World")
            .build();

        when(eventRepo.findAll()).thenReturn(events);
        when(eventSearchResponseMapper.convert(event1)).thenReturn(dto1);
        when(eventSearchResponseMapper.convert(event2)).thenReturn(dto2);

        List<EventSearchDto> result = eventService.searchByTitle("good");

        assertEquals(0, result.size());
    }
}
