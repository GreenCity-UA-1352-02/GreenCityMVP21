package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.EventImageDto;
import greencity.entity.event.Event;
import greencity.entity.event.EventImage;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventImageRepo;
import greencity.repository.EventRepo;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EventImageServiceImpl implements EventImageService {
    private final EventImageRepo eventImageRepo;
    private final EventRepo eventRepo;
    private final FileService fileService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public EventImageDto uploadImage(MultipartFile image, Long eventId) {
        if (!eventRepo.existsById(eventId)) {
            throw new NotFoundException(ErrorMessage.WRONG_EVENT_ID);
        }
        return upload(image, eventId);
    }

    private EventImageDto upload(MultipartFile image, Long eventId) {
        String link = fileService.upload(image);
        EventImage eventImage = EventImage.builder()
            .link(link)
            .event(Event.builder().id(eventId).build())
            .build();

        return modelMapper.map(eventImageRepo.save(eventImage), EventImageDto.class);
    }

    @Override
    @Transactional
    public List<EventImageDto> uploadImages(List<MultipartFile> images, Long eventId) {
        if (!eventRepo.existsById(eventId)) {
            throw new NotFoundException(ErrorMessage.WRONG_EVENT_ID);
        }

        return images.stream()
            .map(image -> upload(image, eventId))
            .toList();
    }

    @Override
    @Transactional
    public void deleteImagesByEventId(Long eventId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.WRONG_EVENT_ID));

        event.getImages().forEach(this::deleteImage);
        event.getImages().clear();
        eventRepo.save(event);
    }

    private void deleteImage(EventImage image) {
        fileService.delete(image.getLink());
        eventImageRepo.delete(image);
    }

    @Override
    public void deleteImagesByEventIdExceptMain(Long eventId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.WRONG_EVENT_ID));

        Long mainImageId = event.getMainImage().getId();
        event.getImages().stream()
            .filter(image -> isNotMainImage(image, mainImageId))
            .forEach(this::deleteImage);
        event.getImages().removeIf(image -> isNotMainImage(image, mainImageId));
        eventRepo.save(event);
    }

    private boolean isNotMainImage(EventImage image, Long mainImageId) {
        return !Objects.equals(image.getId(), mainImageId);
    }
}
