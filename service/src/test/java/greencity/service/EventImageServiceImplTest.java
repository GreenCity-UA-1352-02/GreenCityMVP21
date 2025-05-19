package greencity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventImageDto;
import greencity.entity.event.Event;
import greencity.entity.event.EventImage;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventImageRepo;
import greencity.repository.EventRepo;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class EventImageServiceImplTest {
    @InjectMocks
    private EventImageServiceImpl eventImageService;

    @Mock
    private EventImageRepo eventImageRepo;

    @Mock
    private EventRepo eventRepo;

    @Mock
    private FileService fileService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private MultipartFile multipartFile;

    private final Long eventId = 1L;

    @Test
    void uploadImage_success() {
        when(eventRepo.existsById(eventId)).thenReturn(true);
        when(fileService.upload(multipartFile)).thenReturn("https://link.to/image.jpg");

        EventImage savedImage = EventImage.builder()
            .id(2L)
            .link("https://link.to/image.jpg")
            .event(Event.builder().id(eventId).build())
            .build();

        EventImageDto expectedDto = new EventImageDto(2L, "https://link.to/image.jpg");

        when(eventImageRepo.save(any(EventImage.class))).thenReturn(savedImage);
        when(modelMapper.map(savedImage, EventImageDto.class)).thenReturn(expectedDto);

        EventImageDto result = eventImageService.uploadImage(multipartFile, eventId);

        assertEquals(expectedDto, result);
        verify(fileService).upload(multipartFile);
        verify(eventImageRepo).save(any(EventImage.class));
    }

    @Test
    void uploadImage_EventNotFound_ThrownException() {
        when(eventRepo.existsById(eventId)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class,
            () -> eventImageService.uploadImage(multipartFile, eventId));

        assertEquals(ErrorMessage.WRONG_EVENT_ID, ex.getMessage());
    }

    @Test
    void uploadImages_success() {
        when(eventRepo.existsById(eventId)).thenReturn(true);
        when(fileService.upload(multipartFile)).thenReturn("https://link.to/image.jpg");

        EventImage savedImage = EventImage.builder()
            .id(2L)
            .link("https://link.to/image.jpg")
            .event(Event.builder().id(eventId).build())
            .build();

        EventImageDto expectedDto = new EventImageDto(2L, "https://link.to/image.jpg");

        when(eventImageRepo.save(any(EventImage.class))).thenReturn(savedImage);
        when(modelMapper.map(savedImage, EventImageDto.class)).thenReturn(expectedDto);

        List<EventImageDto> result = eventImageService.uploadImages(List.of(multipartFile), eventId);

        assertEquals(1, result.size());
        assertEquals(expectedDto, result.get(0));
    }

    @Test
    void uploadImages_EventNotFound_ThrownException() {
        when(eventRepo.existsById(eventId)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class,
            () -> eventImageService.uploadImages(List.of(multipartFile), eventId));

        assertEquals(ErrorMessage.WRONG_EVENT_ID, ex.getMessage());
    }

    @Test
    void deleteImagesByEventId_success() {
        when(eventRepo.findById(eventId)).thenReturn(Optional.ofNullable(ModelUtils.getEvent()));

        eventImageService.deleteImagesByEventId(eventId);

        verify(eventRepo).save(any(Event.class));
        verify(fileService).delete(ModelUtils.getEventImages().getFirst().getLink());
        verify(eventImageRepo).delete(ModelUtils.getEventImages().getFirst());
    }

    @Test
    void deleteImagesByEventId_eventNotFound() {
        when(eventRepo.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventImageService.deleteImagesByEventId(eventId));
    }

    @Test
    void deleteImagesByEventIdExceptMain_Success() {
        when(eventRepo.findById(eventId)).thenReturn(Optional.ofNullable(ModelUtils.getEvent()));

        eventImageService.deleteImagesByEventIdExceptMain(eventId);

        verify(eventRepo).save(any(Event.class));
        verify(fileService, atLeastOnce()).delete(anyString());
        verify(eventImageRepo, atLeastOnce()).delete(any());
    }

    @Test
    void deleteImagesByEventIdExceptMain_eventNotFound() {
        when(eventRepo.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventImageService.deleteImagesByEventIdExceptMain(eventId));
    }
}
