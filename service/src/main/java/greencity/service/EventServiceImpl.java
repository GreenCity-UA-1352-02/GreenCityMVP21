package greencity.service;

import greencity.client.RestClient;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventImage;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    private final RestClient restClient;

    @Override
    public EventDto save(AddEventDtoRequest addEventDtoRequest, List<MultipartFile> images, String email) {
        Event event = modelMapper.map(addEventDtoRequest, Event.class);

        UserVO user = restClient.findByEmail(email);
        User author = modelMapper.map(user, User.class);
        event.setAuthor(author);

        images.forEach(image -> {
            String link = fileService.upload(image);
            event.getImages().add(
                    EventImage.builder()
                            .link(link)
                            .event(event)
                            .build()
            );
        });

        eventRepo.save(event);

        return modelMapper.map(event, EventDto.class);
    }
}
