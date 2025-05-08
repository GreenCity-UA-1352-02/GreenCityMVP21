package greencity.service;

import greencity.dto.event.EventImageDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EventImageService {
    /**
     * Uploads an image and associates it with a specific event.
     *
     * @param image  the image to upload
     * @param eventId  the ID of the event to associate with the image
     * @return details of the uploaded image
     */
    EventImageDto uploadImage(MultipartFile image, Long eventId);

    /**
     * Uploads multiple images and associates them with an event.
     *
     * @param images   the images to upload
     * @param eventId  the id of the event to associate the images with
     * @return the list of uploaded image details
     */
    List<EventImageDto> uploadImages(List<MultipartFile> images, Long eventId);

    /**
     * Deletes all images associated with an event.
     *
     * @param eventId  the ID of the event
     */
    void deleteImagesByEventId(Long eventId);
}
