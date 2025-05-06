package greencity.service;

import greencity.dto.event.AddEventRequest;
import greencity.dto.event.EventDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {
    /**
     * Method saves event.
     *
     * @param addEventRequest - event to be saved
     * @param images             - images of event
     * @param email              - email of user
     * @return saved event
     */
    EventDto save(AddEventRequest addEventRequest, List<MultipartFile> images, String email);


    /**
     * Updates an existing Event.
     *
     * @param eventDto the {@link EventDto} object containing updated event details.
     * @param images a {@link List} of {@link MultipartFile} objects representing updated images for the event.
     * @return the updated {@link EventDto}.
     */
    EventDto update(EventDto eventDto, List<MultipartFile> images);

    /**
     * Deletes an Event by its unique identifier.
     *
     * @param id the unique identifier of the entity to be deleted
     */
    void delete(Long id);
}
