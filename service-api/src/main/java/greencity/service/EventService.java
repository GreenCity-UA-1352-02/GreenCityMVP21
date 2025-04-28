package greencity.service;

import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {
    /**
     * Method saves event.
     *
     * @param addEventDtoRequest - event to be saved
     * @param images             - images of event
     * @param email              - email of user
     * @return saved event
     */
    EventDto save(AddEventDtoRequest addEventDtoRequest, List<MultipartFile> images, String email);
}
